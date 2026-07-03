package com.iot.integrations.airthings.adapter;

import com.iot.integrations.airthings.client.dto.AirthingsDevice;
import com.iot.integrations.airthings.client.dto.AirthingsDeviceResponse;
import com.iot.integrations.airthings.client.dto.AirthingsSensorResponse;
import com.iot.integrations.airthings.client.dto.AirthingsSensorResult;
import com.iot.shared.enums.DeviceBrand;
import com.iot.shared.model.DeviceData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class AirthingsAdapter {

    public List<DeviceData> adapt(
            AirthingsDeviceResponse devicesResponse,
            AirthingsSensorResponse sensorsResponse) {

        List<DeviceData> result = new ArrayList<>();

        for (AirthingsDevice device : devicesResponse.getDevices()) {

            // Trouve les sensors de ce device
            AirthingsSensorResult deviceSensors = sensorsResponse.getResults()
                    .stream()
                    .filter(s -> device.getSerialNumber().equals(s.getSerialNumber()))
                    .findFirst()
                    .orElse(null);

            // Extrait les mesures
            Map<String, Object> measurements = new HashMap<>();
            if (deviceSensors != null) {
                deviceSensors.getSensors().forEach(sensor ->
                        measurements.put(sensor.getSensorType(), sensor.getValue())
                );
            }

            log.debug("Device {} - mesures : {}", device.getName(), measurements);

            DeviceData data = DeviceData.builder()
                    .deviceId(device.getSerialNumber())
                    .name(device.getName())
                    .type("AIR_QUALITY")
                    .source(DeviceBrand.AIRTHINGS)
                    .sensors(measurements)
                    .timestamp(LocalDateTime.now())
                    .build();

            result.add(data);
        }

        log.info("Adaptation terminée : {} devices traités", result.size());
        return result;
    }
}
