package com.iot.alerts.service;

import com.iot.alerts.model.Alert;
import com.iot.alerts.port.out.AlertRepository;
import com.iot.alerts.port.out.NotificationPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NotificationService implements NotificationPort {

    private final AlertRepository alertRepository;
    private final PushNotificationService pushNotificationService;

    public NotificationService(AlertRepository alertRepository, PushNotificationService pushNotificationService) {
        this.alertRepository = alertRepository;
        this.pushNotificationService = pushNotificationService;
    }

    @Override
    public void sendNotification(Alert alert) {
        log.warn("🚨 ALERTE : Device {} - {} = {} → {}",
                alert.getDeviceId(),
                alert.getSensorType(),
                alert.getValue(),
                alert.getMessage());

        String title = "🚨 Alerte IoT Hub";
        String body = alert.getSensorType() + " : " + alert.getMessage();
        pushNotificationService.sendToAll(title, body);
    }
}
