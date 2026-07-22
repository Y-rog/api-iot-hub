package com.iot.alerts.service;

import com.iot.alerts.model.PushSubscription;
import com.iot.alerts.port.out.PushSubscriptionRepository;
import lombok.extern.slf4j.Slf4j;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Security;

@Slf4j
@Service
public class PushNotificationService {

    private final PushSubscriptionRepository subscriptionRepository;
    private final PushService pushService;

    public PushNotificationService(
            PushSubscriptionRepository subscriptionRepository,
            @Value("${vapid.public-key}") String publicKey,
            @Value("${vapid.private-key}") String privateKey,
            @Value("${vapid.subject}") String subject) throws Exception {

        Security.addProvider(new BouncyCastleProvider());
        this.subscriptionRepository = subscriptionRepository;
        this.pushService = new PushService(publicKey, privateKey, subject);
    }

    public void sendToAll(String title, String body) {
        subscriptionRepository.findAll().forEach(sub -> sendToOne(sub, title, body));
    }

    private void sendToOne(PushSubscription sub, String title, String body) {
        try {
            String payload = String.format(
                    "{\"notification\":{\"title\":\"%s\",\"body\":\"%s\"}}",
                    title, body
            );

            nl.martijndwars.webpush.Subscription subscription = new nl.martijndwars.webpush.Subscription(
                    sub.getEndpoint(),
                    new nl.martijndwars.webpush.Subscription.Keys(sub.getP256dh(), sub.getAuth())
            );

            Notification notification = new Notification(subscription, payload);
            pushService.send(notification);

            log.info("Notification push envoyée à {}", sub.getEndpoint());
        } catch (Exception e) {
            log.error("Erreur envoi push à {} : {}", sub.getEndpoint(), e.getMessage());
        }
    }
}
