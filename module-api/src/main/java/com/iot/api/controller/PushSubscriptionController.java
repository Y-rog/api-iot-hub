package com.iot.api.controller;

import com.iot.alerts.model.PushSubscription;
import com.iot.alerts.port.out.PushSubscriptionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/push")
public class PushSubscriptionController {

    private final PushSubscriptionRepository repository;

    @Value("${vapid.public-key}")
    private String vapidPublicKey;

    public PushSubscriptionController(PushSubscriptionRepository repository) {
        this.repository = repository;
    }

    public record SubscriptionDTO(String endpoint, KeysDTO keys) {}
    public record KeysDTO(String p256dh, String auth) {}

    @GetMapping("/vapid-public-key")
    public ResponseEntity<String> getPublicKey() {
        return ResponseEntity.ok(vapidPublicKey);
    }

    @PostMapping("/subscribe")
    public ResponseEntity<Void> subscribe(@RequestBody SubscriptionDTO dto) {
        PushSubscription subscription = PushSubscription.builder()
                .endpoint(dto.endpoint())
                .p256dh(dto.keys().p256dh())
                .auth(dto.keys().auth())
                .createdAt(LocalDateTime.now())
                .build();

        repository.save(subscription);
        return ResponseEntity.ok().build();
    }
}
