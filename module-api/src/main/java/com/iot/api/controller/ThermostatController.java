package com.iot.api.controller;

import com.iot.api.dto.ThermostatCommandDTO;
import com.iot.history.port.in.HistoryUseCase;
import com.iot.integrations.sinope.publisher.MqttPublisher;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/thermostats")
public class ThermostatController {

    private final MqttPublisher mqttPublisher;
    private final HistoryUseCase historyUseCase;

    public ThermostatController(MqttPublisher mqttPublisher, HistoryUseCase historyUseCase) {
        this.mqttPublisher = mqttPublisher;
        this.historyUseCase = historyUseCase;
    }

    @GetMapping("/{id}/temperature")
    public ResponseEntity<ThermostatCommandDTO> getTemperature(@PathVariable String id) {
        return historyUseCase.getHistory(id)
                .stream()
                .filter(dp -> dp.getSensorType().equals("temperature"))
                .findFirst()
                .map(dp -> ResponseEntity.ok(new ThermostatCommandDTO(dp.getValue())))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/temperature")
    public ResponseEntity<Void> setTemperature(
            @PathVariable String id,
            @Valid @RequestBody ThermostatCommandDTO dto) {

        mqttPublisher.setTemperature(id, dto.temperature());
        return ResponseEntity.ok().build();
    }
}
