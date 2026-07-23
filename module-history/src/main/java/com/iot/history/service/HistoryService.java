package com.iot.history.service;

import com.iot.history.model.DataPoint;
import com.iot.history.port.in.HistoryUseCase;
import com.iot.history.port.out.HistoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import com.iot.shared.event.DeviceDataEvent;
import com.iot.shared.model.DeviceData;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class HistoryService implements HistoryUseCase {

    private final HistoryRepository historyRepository;

    public HistoryService(HistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
    }

    @Override
    public List<DataPoint> getHistory(String deviceId) {
        return historyRepository.findByDeviceId(deviceId);
    }

    @Override
    public DataPoint registerDataPoint(DataPoint dataPoint) {
        DataPoint dataPointWithId = dataPoint.toBuilder()
                .id(UUID.randomUUID().toString())
                .build();
        return historyRepository.createDataPoint(dataPointWithId);
    }

    @EventListener
    public void onDeviceData(DeviceDataEvent event) {
        DeviceData data = event.getDeviceData();
        log.debug("Sauvegarde historique pour device : {}", data.getName());

        int savedCount = 0;

        for (var entry : data.getSensors().entrySet()) {
            String sensorType = entry.getKey();
            Object value = entry.getValue();

            // Certains appareils envoient parfois une mesure absente (null),
            // par exemple "energy" ou "power" sur un thermostat qui vient de
            // redémarrer — on l'ignore proprement plutôt que de planter
            if (value == null) {
                log.warn("Mesure ignorée : {} {} = null (device {})",
                        data.getDeviceId(), sensorType, data.getName());
                continue;
            }

            log.debug("DataPoint : {} {} = {}", data.getDeviceId(), sensorType, value);

            DataPoint dataPoint = DataPoint.builder()
                    .deviceId(data.getDeviceId())
                    .sensorType(sensorType)
                    .value(((Number) value).doubleValue())
                    .timestamp(LocalDateTime.now())
                    .build();

            registerDataPoint(dataPoint);
            savedCount++;
        }

        log.info("Historique sauvegardé : {} mesures pour {}", savedCount, data.getName());
    }
}
