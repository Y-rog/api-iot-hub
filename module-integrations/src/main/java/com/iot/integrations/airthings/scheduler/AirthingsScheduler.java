package com.iot.integrations.airthings.scheduler;

import com.iot.integrations.airthings.adapter.AirthingsAdapter;
import com.iot.integrations.airthings.client.AirthingsClient;
import com.iot.integrations.airthings.client.dto.AirthingsDeviceResponse;
import com.iot.integrations.airthings.client.dto.AirthingsSensorResponse;
import com.iot.shared.event.DeviceDataEvent;
import com.iot.shared.model.DeviceData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
public class AirthingsScheduler {

    private final AirthingsClient client;
    private final AirthingsAdapter adapter;
    private final ApplicationEventPublisher eventPublisher;

    public AirthingsScheduler(AirthingsClient client,
                              AirthingsAdapter adapter,
                              ApplicationEventPublisher eventPublisher) {
        this.client = client;
        this.adapter = adapter;
        this.eventPublisher = eventPublisher;
    }

    @Scheduled(fixedDelay = 300000, initialDelay = 0)
    public void poll() {
        log.info("Démarrage polling Airthings...");
        try {
            String token = client.getToken();
            AirthingsDeviceResponse devices = client.getDevices(token);
            AirthingsSensorResponse sensors = client.getSensors(token);

            List<DeviceData> data = adapter.adapt(devices, sensors);

            data.forEach(d -> {
                log.info("Publication event pour : {}", d.getName());
                eventPublisher.publishEvent(DeviceDataEvent.builder()
                        .deviceData(d)
                        .occurredAt(LocalDateTime.now())
                        .build());
            });

        } catch (Exception e) {
            log.error("Erreur polling Airthings : {}", e.getMessage());
        }
    }
}
