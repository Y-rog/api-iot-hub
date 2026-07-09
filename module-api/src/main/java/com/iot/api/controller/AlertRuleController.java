package com.iot.api.controller;

import com.iot.alerts.model.AlertRule;
import com.iot.alerts.port.in.AlertRuleUseCase;
import com.iot.api.dto.AlertRuleDTO;
import com.iot.api.mapper.AlertRuleMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/alertRules")
public class AlertRuleController {

    private final AlertRuleUseCase alertRuleUseCase;
    private final AlertRuleMapper alertRuleMapper;

    public AlertRuleController(AlertRuleUseCase alertRuleUseCase, AlertRuleMapper alertRuleMapper) {
        this.alertRuleUseCase = alertRuleUseCase;
        this.alertRuleMapper = alertRuleMapper;
    }

    @GetMapping
    public List<AlertRuleDTO> getAlertRules() {
        return alertRuleUseCase.getAllAlertRules()
                .stream()
                .map(alertRuleMapper::toDTO)
                .toList();
    }

    @PostMapping
    public ResponseEntity<AlertRuleDTO> createAlertRule(@RequestBody AlertRuleDTO dto) {
        AlertRule created = alertRuleUseCase.createAlertRule(alertRuleMapper.toEntity(dto));
        return ResponseEntity.created(URI.create("/api/alerRules/" + created.getId()))
                .body(alertRuleMapper.toDTO(created));
    }

    @GetMapping("{id}")
    public AlertRuleDTO getAlertRule(@PathVariable String id) {
        AlertRule alertRule = alertRuleUseCase.getAlertRule(id);
        return alertRuleMapper.toDTO(alertRule);
    }

    @PutMapping("{id}")
    public ResponseEntity<AlertRuleDTO> updateAlertRule(@PathVariable String id, @RequestBody AlertRuleDTO dto) {
        AlertRule alertRule = alertRuleMapper.toEntity(dto)
                .toBuilder()
                .id(id)
                .build();
        AlertRule updated = alertRuleUseCase.updateAlertRule(alertRule);
        return ResponseEntity.ok()
                .location(URI.create("/api/alerRules/" + updated.getId()))
                .body(alertRuleMapper.toDTO(updated));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteAlertRule(@PathVariable String id) {
        alertRuleUseCase.deleteAlertRule(id);
        return ResponseEntity.noContent().build();
    }


}
