package com.iot.alerts.service;

import com.iot.alerts.model.Alert;
import com.iot.alerts.model.AlertRule;
import com.iot.alerts.port.out.AlertRepository;
import com.iot.alerts.port.out.AlertRuleRepository;
import com.iot.alerts.port.out.NotificationPort;
import com.iot.shared.enums.AlertOperator;
import com.iot.shared.enums.DeviceBrand;
import com.iot.shared.enums.DeviceType;
import com.iot.shared.event.DeviceDataEvent;
import com.iot.shared.model.DeviceData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlertServiceTest {

    @Mock
    private AlertRepository alertRepository;

    @Mock
    private AlertRuleRepository alertRuleRepository;

    @Mock
    private NotificationPort notificationPort;

    @InjectMocks
    private AlertService alertService;

    @Test
    void should_trigger_alert_when_co2_exceeds_threshold() {
        // GIVEN
        // Une règle : co2 > 1000
        AlertRule rule = AlertRule.builder()
                .id("rule-1")
                .sensorType("co2")
                .operator(AlertOperator.GREATER_THAN)
                .threshold(1000.0)
                .message("CO2 trop élevé !")
                .build();

        // Un device avec co2 = 1200
        DeviceData data = DeviceData.builder()
                .deviceId("device-1")
                .name("Salon")
                .type(DeviceType.AIR_QUALITY)
                .source(DeviceBrand.AIRTHINGS)
                .sensors(Map.of("co2", 1200.0))
                .timestamp(LocalDateTime.now())
                .build();

        DeviceDataEvent event = DeviceDataEvent.builder()
                .deviceData(data)
                .occurredAt(LocalDateTime.now())
                .build();

        // Mock → retourne la règle
        when(alertRuleRepository.findAll()).thenReturn(List.of(rule));
        // Mock → pas d'alerte récente (cooldown inactif)
        when(alertRepository.existsRecentAlert(any(), any(), any())).thenReturn(false);
        // Mock → sauvegarde retourne l'alerte
        when(alertRepository.saveAlert(any())).thenAnswer(i -> i.getArgument(0));

        // WHEN
        alertService.onDeviceData(event);

        // THEN → une alerte sauvegardée et notification envoyée
        verify(alertRepository, times(1)).saveAlert(any());
        verify(notificationPort, times(1)).sendNotification(any());
    }

    @Test
    void should_not_trigger_alert_when_co2_below_threshold() {
        // GIVEN
        AlertRule rule = AlertRule.builder()
                .id("rule-1")
                .sensorType("co2")
                .operator(AlertOperator.GREATER_THAN)
                .threshold(1000.0)
                .message("CO2 trop élevé !")
                .build();

        // co2 = 800 → sous le seuil
        DeviceData data = DeviceData.builder()
                .deviceId("device-1")
                .name("Salon")
                .type(DeviceType.AIR_QUALITY)
                .source(DeviceBrand.AIRTHINGS)
                .sensors(Map.of("co2", 800.0))
                .timestamp(LocalDateTime.now())
                .build();

        DeviceDataEvent event = DeviceDataEvent.builder()
                .deviceData(data)
                .occurredAt(LocalDateTime.now())
                .build();

        when(alertRuleRepository.findAll()).thenReturn(List.of(rule));

        // WHEN
        alertService.onDeviceData(event);

        // THEN → aucune alerte créée !
        verify(alertRepository, never()).saveAlert(any());
        verify(notificationPort, never()).sendNotification(any());
    }

    @Test
    void should_not_send_notification_when_cooldown_active() {
        // GIVEN
        AlertRule rule = AlertRule.builder()
                .id("rule-1")
                .sensorType("co2")
                .operator(AlertOperator.GREATER_THAN)
                .threshold(1000.0)
                .message("CO2 trop élevé !")
                .build();

        DeviceData data = DeviceData.builder()
                .deviceId("device-1")
                .name("Salon")
                .type(DeviceType.AIR_QUALITY)
                .source(DeviceBrand.AIRTHINGS)
                .sensors(Map.of("co2", 1200.0))
                .timestamp(LocalDateTime.now())
                .build();

        DeviceDataEvent event = DeviceDataEvent.builder()
                .deviceData(data)
                .occurredAt(LocalDateTime.now())
                .build();

        when(alertRuleRepository.findAll()).thenReturn(List.of(rule));
        // Mock → alerte récente existe (cooldown actif !)
        when(alertRepository.existsRecentAlert(any(), any(), any())).thenReturn(true);
        when(alertRepository.saveAlert(any())).thenAnswer(i -> i.getArgument(0));

        // WHEN
        alertService.onDeviceData(event);

        // THEN → alerte sauvegardée mais PAS de notification !
        verify(alertRepository, times(1)).saveAlert(any());
        verify(notificationPort, never()).sendNotification(any());
    }

    @Test
    void should_do_nothing_when_device_has_no_matching_sensor() {
        // GIVEN
        // Une règle sur le radon...
        AlertRule rule = AlertRule.builder()
                .id("rule-1")
                .sensorType("radonShortTermAvg")
                .operator(AlertOperator.GREATER_THAN)
                .threshold(150.0)
                .message("Radon trop élevé !")
                .build();

        // ...mais le device n'a que la température, pas de mesure de radon
        DeviceData data = DeviceData.builder()
                .deviceId("device-2")
                .name("Thermostat Salon")
                .type(DeviceType.THERMOSTAT)
                .source(DeviceBrand.SINOPE)
                .sensors(Map.of("temperature", 21.0))
                .timestamp(LocalDateTime.now())
                .build();

        DeviceDataEvent event = DeviceDataEvent.builder()
                .deviceData(data)
                .occurredAt(LocalDateTime.now())
                .build();

        when(alertRuleRepository.findAll()).thenReturn(List.of(rule));

        // WHEN
        alertService.onDeviceData(event);

        // THEN → rien ne se passe, pas d'erreur, pas d'alerte
        verify(alertRepository, never()).saveAlert(any());
        verify(notificationPort, never()).sendNotification(any());
    }

    @Test
    void should_evaluate_each_rule_independently_when_multiple_rules_exist() {
        // GIVEN
        // Deux règles différentes, sur deux capteurs différents
        AlertRule co2Rule = AlertRule.builder()
                .id("rule-1")
                .sensorType("co2")
                .operator(AlertOperator.GREATER_THAN)
                .threshold(1000.0)
                .message("CO2 trop élevé !")
                .build();

        AlertRule vocRule = AlertRule.builder()
                .id("rule-2")
                .sensorType("voc")
                .operator(AlertOperator.GREATER_THAN)
                .threshold(250.0)
                .message("VOC trop élevé !")
                .build();

        // Le device dépasse le seuil CO2, mais pas le seuil VOC
        DeviceData data = DeviceData.builder()
                .deviceId("device-1")
                .name("Salon")
                .type(DeviceType.AIR_QUALITY)
                .source(DeviceBrand.AIRTHINGS)
                .sensors(Map.of("co2", 1500.0, "voc", 50.0))
                .timestamp(LocalDateTime.now())
                .build();

        DeviceDataEvent event = DeviceDataEvent.builder()
                .deviceData(data)
                .occurredAt(LocalDateTime.now())
                .build();

        when(alertRuleRepository.findAll()).thenReturn(List.of(co2Rule, vocRule));
        when(alertRepository.existsRecentAlert(any(), any(), any())).thenReturn(false);
        when(alertRepository.saveAlert(any())).thenAnswer(i -> i.getArgument(0));

        // WHEN
        alertService.onDeviceData(event);

        // THEN → une seule alerte créée (CO2), pas deux
        verify(alertRepository, times(1)).saveAlert(any());
        verify(notificationPort, times(1)).sendNotification(any());
    }

    @Test
    void should_trigger_alert_with_less_than_operator() {
        // GIVEN
        // Une règle inhabituelle : température < 10°C (mode hors gel)
        AlertRule rule = AlertRule.builder()
                .id("rule-1")
                .sensorType("temperature")
                .operator(AlertOperator.LESS_THAN)
                .threshold(10.0)
                .message("Risque de gel !")
                .build();

        DeviceData data = DeviceData.builder()
                .deviceId("device-3")
                .name("Thermostat Sous-sol")
                .type(DeviceType.THERMOSTAT)
                .source(DeviceBrand.SINOPE)
                .sensors(Map.of("temperature", 5.0))
                .timestamp(LocalDateTime.now())
                .build();

        DeviceDataEvent event = DeviceDataEvent.builder()
                .deviceData(data)
                .occurredAt(LocalDateTime.now())
                .build();

        when(alertRuleRepository.findAll()).thenReturn(List.of(rule));
        when(alertRepository.existsRecentAlert(any(), any(), any())).thenReturn(false);
        when(alertRepository.saveAlert(any())).thenAnswer(i -> i.getArgument(0));

        // WHEN
        alertService.onDeviceData(event);

        // THEN → l'opérateur LESS_THAN fonctionne bien, pas juste GREATER_THAN
        verify(alertRepository, times(1)).saveAlert(any());
        verify(notificationPort, times(1)).sendNotification(any());
    }
}
