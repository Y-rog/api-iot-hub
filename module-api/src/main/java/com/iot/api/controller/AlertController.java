package com.iot.api.controller;

import com.iot.alerts.model.Alert;
import com.iot.alerts.port.in.AlertUseCase;
import com.iot.api.dto.AlertDTO;
import com.iot.api.mapper.AlertMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/alerts")
public class AlertController {

    private final AlertUseCase alertUseCase;
    private final AlertMapper alertMapper;

    public AlertController(AlertUseCase alertUseCase, AlertMapper alertMapper) {
        this.alertUseCase = alertUseCase;
        this.alertMapper = alertMapper;
    }

    @GetMapping
    public List<AlertDTO> getAlerts(){
        return alertUseCase.getAllAlerts()
                .stream()
                .map(alertMapper::toDTO)
                .toList();
    }

    @PostMapping
    public ResponseEntity<AlertDTO> createAlert(@RequestBody AlertDTO dto) {
        Alert created = alertUseCase.createAlert(alertMapper.toEntity(dto));
        return ResponseEntity.created(URI.create("/api/alerts/" + created.getId()))
                .body(alertMapper.toDTO(created));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<AlertDTO> markAsRead(@PathVariable String id) {
        return alertUseCase.markAsRead(id)
                .map(alertMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}
