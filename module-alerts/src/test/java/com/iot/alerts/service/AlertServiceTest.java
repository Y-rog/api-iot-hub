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
}
