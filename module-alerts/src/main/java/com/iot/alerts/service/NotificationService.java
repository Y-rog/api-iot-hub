package com.iot.alerts.service;

import com.iot.alerts.model.Alert;
import com.iot.alerts.port.out.AlertRepository;
import com.iot.alerts.port.out.NotificationPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class NotificationService implements NotificationPort {

    private final AlertRepository alertRepository;

    public NotificationService(AlertRepository alertRepository) {
        this.alertRepository = alertRepository;
    }

    @Override
    public void sendNotification(Alert alert) {
        // Vérifie si une alerte similaire existe dans la dernière heure
        boolean recentAlertExists = alertRepository.existsRecentAlert(
                alert.getDeviceId(),
                alert.getSensorType(),
                LocalDateTime.now().minusHours(1)
        );

        if (!recentAlertExists) {
            log.warn("🚨 ALERTE : Device {} - {} = {} → {}",
                    alert.getDeviceId(),
                    alert.getSensorType(),
                    alert.getValue(),
                    alert.getMessage());
            // TODO → envoyer vrai email
        } else {
            log.debug("Alerte ignorée (cooldown) : {} - {}",
                    alert.getDeviceId(),
                    alert.getSensorType());
        }
    }

}
