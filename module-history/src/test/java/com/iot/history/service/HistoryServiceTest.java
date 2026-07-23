package com.iot.history.service;

import com.iot.history.model.DataPoint;
import com.iot.history.port.out.HistoryRepository;
import com.iot.shared.event.DeviceDataEvent;
import com.iot.shared.model.DeviceData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HistoryServiceTest {

    @Mock
    private HistoryRepository historyRepository;

    @InjectMocks
    private HistoryService historyService;

    @Test
    void should_return_history_for_a_given_device() {
        // Vérifie que l'historique demandé correspond bien au device précis
        DataPoint point = DataPoint.builder()
                .deviceId("device-1")
                .sensorType("temperature")
                .value(21.5)
                .build();

        when(historyRepository.findByDeviceId("device-1")).thenReturn(List.of(point));

        List<DataPoint> result = historyService.getHistory("device-1");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getSensorType()).isEqualTo("temperature");
    }

    @Test
    void should_assign_a_new_id_when_registering_a_data_point() {
        // registerDataPoint doit toujours générer un ID lui-même,
        // même si le DataPoint reçu n'en a pas
        DataPoint incoming = DataPoint.builder()
                .deviceId("device-1")
                .sensorType("co2")
                .value(450.0)
                .build();

        when(historyRepository.createDataPoint(any())).thenAnswer(i -> i.getArgument(0));

        DataPoint result = historyService.registerDataPoint(incoming);

        assertThat(result.getId()).isNotNull();
    }

    @Test
    void should_save_one_data_point_per_sensor_when_device_data_received() {
        // Un seul événement peut contenir plusieurs mesures (co2, temp, humidity...) —
        // chacune doit être sauvegardée comme un DataPoint séparé
        DeviceData data = DeviceData.builder()
                .deviceId("device-1")
                .name("View Plus Salon")
                .sensors(Map.of("co2", 450.0, "temperature", 21.5, "humidity", 55.0))
                .build();

        DeviceDataEvent event = DeviceDataEvent.builder()
                .deviceData(data)
                .occurredAt(LocalDateTime.now())
                .build();

        when(historyRepository.createDataPoint(any())).thenAnswer(i -> i.getArgument(0));

        historyService.onDeviceData(event);

        // 3 capteurs dans l'événement → 3 appels de sauvegarde, un par mesure
        verify(historyRepository, times(3)).createDataPoint(any());
    }

    @Test
    void should_store_correct_values_for_each_sensor_type() {
        // Vérifie que chaque DataPoint créé contient bien le bon type et la bonne valeur,
        // pas juste le bon nombre d'appels
        DeviceData data = DeviceData.builder()
                .deviceId("device-2")
                .name("View Plus Chambre")
                .sensors(Map.of("radonShortTermAvg", 8.0))
                .build();

        DeviceDataEvent event = DeviceDataEvent.builder()
                .deviceData(data)
                .occurredAt(LocalDateTime.now())
                .build();

        when(historyRepository.createDataPoint(any())).thenAnswer(i -> i.getArgument(0));

        historyService.onDeviceData(event);

        ArgumentCaptor<DataPoint> captor = ArgumentCaptor.forClass(DataPoint.class);
        verify(historyRepository).createDataPoint(captor.capture());

        DataPoint saved = captor.getValue();
        assertThat(saved.getDeviceId()).isEqualTo("device-2");
        assertThat(saved.getSensorType()).isEqualTo("radonShortTermAvg");
        assertThat(saved.getValue()).isEqualTo(8.0);
    }

    @Test
    void should_do_nothing_when_device_has_no_sensors() {
        // Cas limite : un événement sans aucune mesure ne doit rien sauvegarder,
        // ni faire planter le service
        DeviceData data = DeviceData.builder()
                .deviceId("device-3")
                .name("Device vide")
                .sensors(Map.of())
                .build();

        DeviceDataEvent event = DeviceDataEvent.builder()
                .deviceData(data)
                .occurredAt(LocalDateTime.now())
                .build();

        historyService.onDeviceData(event);

        verify(historyRepository, never()).createDataPoint(any());
    }
}
