package com.iot.alerts.service;

import com.iot.alerts.model.Alert;
import com.iot.alerts.port.out.AlertRepository;
import com.iot.alerts.port.out.NotificationPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class NotificationService implements NotificationPort {

    private final AlertRepository alertRepository;
    private final JavaMailSender mailSender;

    @Value("${notification.email-to}")
    private String emailTo;

    @Value("${spring.mail.username}")
    private String emailFrom;

    public NotificationService(AlertRepository alertRepository, JavaMailSender mailSender) {
        this.alertRepository = alertRepository;
        this.mailSender = mailSender;
    }

    @Override
    public void sendNotification(Alert alert) {
        log.warn("🚨 ALERTE : Device {} - {} = {} → {}",
                alert.getDeviceId(),
                alert.getSensorType(),
                alert.getValue(),
                alert.getMessage());
        sendEmail(alert);
    }

    private void sendEmail(Alert alert) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(emailFrom);
            message.setTo(emailTo);
            message.setSubject("🚨 Alerte IoT Hub — " + alert.getSensorType());
            message.setText(
                    "Alerte déclenchée !\n\n" +
                            "Device    : " + alert.getDeviceId() + "\n" +
                            "Capteur   : " + alert.getSensorType() + "\n" +
                            "Valeur    : " + alert.getValue() + "\n" +
                            "Message   : " + alert.getMessage() + "\n" +
                            "Heure     : " + alert.getCreatedAt()
            );

            mailSender.send(message);
            log.info("Email envoyé à {}", emailTo);

        } catch (Exception e) {
            log.error("Erreur envoi email : {}", e.getMessage());
        }
    }
}
