package com.iot.api.controller;

import com.iot.api.dto.ThermostatCommandDTO;
import com.iot.history.model.DataPoint;
import com.iot.history.port.in.HistoryUseCase;
import com.iot.integrations.sinope.publisher.MqttPublisher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ThermostatControllerTest {

    @Mock
    private MqttPublisher mqttPublisher;

    @Mock
    private HistoryUseCase historyUseCase;

    @InjectMocks
    private ThermostatController thermostatController;

    @Test
    void should_return_the_temperature_when_history_contains_one() {
        // L'historique d'un device contient plusieurs types de mesures —
        // le controller doit filtrer et ne renvoyer que "temperature"
        DataPoint temperaturePoint = DataPoint.builder()
                .deviceId("thermostat-salon")
                .sensorType("temperature")
                .value(21.5)
                .timestamp(LocalDateTime.now())
                .build();

        DataPoint powerPoint = DataPoint.builder()
                .deviceId("thermostat-salon")
                .sensorType("power")
                .value(150.0)
                .timestamp(LocalDateTime.now())
                .build();

        when(historyUseCase.getHistory("thermostat-salon"))
                .thenReturn(List.of(powerPoint, temperaturePoint));

        ResponseEntity<ThermostatCommandDTO> response =
                thermostatController.getTemperature("thermostat-salon");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().temperature()).isEqualTo(21.5);
    }

    @Test
    void should_return_404_when_no_temperature_data_exists() {
        // Un device dont l'historique ne contient aucune mesure de température
        // (par exemple juste après son ajout, avant sa première lecture)
        when(historyUseCase.getHistory("nouveau-device"))
                .thenReturn(List.of());

        ResponseEntity<ThermostatCommandDTO> response =
                thermostatController.getTemperature("nouveau-device");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void should_delegate_temperature_command_to_mqtt_publisher() {
        // Vérifie que setTemperature transmet bien l'ID et la valeur
        // exacte au publisher MQTT, sans transformation ni erreur
        ThermostatCommandDTO dto = new ThermostatCommandDTO(20.5);

        ResponseEntity<Void> response =
                thermostatController.setTemperature("thermostat-chambre", dto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(mqttPublisher, times(1)).setTemperature("thermostat-chambre", 20.5);
    }
}
