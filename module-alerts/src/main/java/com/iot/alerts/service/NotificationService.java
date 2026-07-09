package com.iot.alerts.service;

import com.iot.alerts.model.Alert;
import com.iot.alerts.port.out.NotificationPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NotificationService implements NotificationPort {

    @Override
    public void sendNotification(Alert alert) {
        log.warn("🚨 ALERTE : Device {} - {} = {} → {}",
                alert.getDeviceId(),
                alert.getSensorType(),
                alert.getValue(),
                alert.getMessage());

    }

}
