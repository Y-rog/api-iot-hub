package com.iot.integrations.sinope.adapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iot.shared.enums.DeviceBrand;
import com.iot.shared.enums.DeviceType;
import com.iot.shared.model.DeviceData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class SinopeAdapter {

    private final ObjectMapper objectMapper;

    public DeviceData adapt(String topic, String payload) {
        try {
            // 1. Extrait le nom du thermostat depuis le topic
            // "zigbee2mqtt/thermostat-chambre-ethan" → "thermostat-chambre-ethan"
            String name = topic.replace("zigbee2mqtt/", "");

            // 2. Parse le JSON
            Map<String, Object> data = objectMapper.readValue(payload, Map.class);

            // 3. Extrait les mesures
            Map<String, Object> sensors = new HashMap<>();
            sensors.put("temperature", data.get("local_temperature"));
            sensors.put("power", data.get("power"));
            sensors.put("voltage", data.get("voltage"));
            sensors.put("current", data.get("current"));
            sensors.put("energy", data.get("energy"));

            log.debug("Sinopé {} → mesures : {}", name, sensors);

            // 4. Crée le DeviceData
            return DeviceData.builder()
                    .deviceId(name)
                    .name(name)
                    .type(DeviceType.THERMOSTAT)
                    .source(DeviceBrand.SINOPE)
                    .sensors(sensors)
                    .timestamp(LocalDateTime.now())
                    .build();

        } catch (Exception e) {
            log.error("Erreur adaptation Sinopé : {}", e.getMessage());
            return null;
        }
    }
}
