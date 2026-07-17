package com.iot.alerts.service;

import com.iot.alerts.model.Alert;
import com.iot.alerts.model.AlertRule;
import com.iot.alerts.port.in.AlertUseCase;
import com.iot.alerts.port.out.AlertRepository;
import com.iot.alerts.port.out.AlertRuleRepository;
import com.iot.alerts.port.out.NotificationPort;
import com.iot.shared.model.DeviceData;
import com.iot.shared.event.DeviceDataEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class AlertService implements AlertUseCase {

    private final AlertRepository alertRepository;
    private final AlertRuleRepository alertRuleRepository;
    private final NotificationPort notificationPort;
    public AlertService(AlertRepository alertRepository, AlertRuleRepository alertRuleRepository, NotificationPort notificationPort) {
        this.alertRepository = alertRepository;
        this.alertRuleRepository = alertRuleRepository;
        this.notificationPort = notificationPort;
    }


    @Override
    public Alert createAlert(Alert alert) {
        return alertRepository.saveAlert(alert);
    }

    @Override
    public List<Alert> getAllAlerts() {
        return alertRepository.findAll();
    }

    @Override
    public Optional<Alert> markAsRead(String id) {
        return alertRepository.markAsRead(id);
    }

    @EventListener
    public void onDeviceData(DeviceDataEvent event) {
        DeviceData data = event.getDeviceData();
        log.debug("Vérification des règles pour device : {}", data.getName());
        List<AlertRule> rules = alertRuleRepository.findAll();

        for(AlertRule rule : rules) {
            // Récupère la valeur du capteur concerné
            Double value = (Double) data.getSensors().get(rule.getSensorType());
            if (value != null && checkCondition(value, rule)) {

                // Vérifie le cooldown AVANT de créer l'alerte
                boolean recentAlertExists = alertRepository.existsRecentAlert(
                        data.getDeviceId(),
                        rule.getSensorType(),
                        LocalDateTime.now().minusHours(1)
                );

                Alert alert = Alert.builder()
                        .id(UUID.randomUUID().toString())
                        .deviceId(data.getDeviceId())
                        .sensorType(rule.getSensorType())
                        .value(value)
                        .message(rule.getMessage())
                        .createdAt(LocalDateTime.now())
                        .read(false)
                        .build();

                alertRepository.saveAlert(alert);

                // Envoie notification seulement si pas de cooldown
                if (!recentAlertExists) {
                    notificationPort.sendNotification(alert);
                }
            }
        }
    }

    private boolean checkCondition(Double value, AlertRule rule) {
        return switch (rule.getOperator()) {
            case GREATER_THAN          -> value > rule.getThreshold();
            case LESS_THAN             -> value < rule.getThreshold();
            case EQUALS                -> value.equals(rule.getThreshold());
            case GREATER_THAN_OR_EQUAL -> value >= rule.getThreshold();
            case LESS_THAN_OR_EQUAL    -> value <= rule.getThreshold();
        };
    }

}
