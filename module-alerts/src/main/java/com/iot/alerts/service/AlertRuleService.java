package com.iot.alerts.service;

import com.iot.alerts.exception.AlertRuleNotFoundException;
import com.iot.alerts.model.AlertRule;
import com.iot.alerts.port.in.AlertRuleUseCase;
import com.iot.alerts.port.out.AlertRuleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class AlertRuleService implements AlertRuleUseCase {

    private final AlertRuleRepository alertRuleRepository;

    public AlertRuleService(AlertRuleRepository alertRuleRepository) {
        this.alertRuleRepository = alertRuleRepository;
    }


    @Override
    public AlertRule createAlertRule(AlertRule rule) {
        log.info("Création règle : {} {} {}",
                rule.getSensorType(), rule.getOperator(), rule.getThreshold());
        return alertRuleRepository.saveAlertRule(rule);
    }

    @Override
    public AlertRule getAlertRule(String id) {
        log.debug("Recherche règle : {}", id);
        return alertRuleRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Règle introuvable : {}", id);
                    return new AlertRuleNotFoundException(id);
                });
    }

    @Override
    public List<AlertRule> getAllAlertRules() {
        log.debug("Récupération de toutes les règles");
        return alertRuleRepository.findAll();
    }

    @Override
    public AlertRule updateAlertRule(AlertRule rule) {
        log.info("Mise à jour règle : {}", rule.getId());
        return alertRuleRepository.saveAlertRule(rule);
    }

    @Override
    public void deleteAlertRule(String id) {
        log.info("Suppression règle : {}", id);
        alertRuleRepository.deleteAlertRule(id);
    }
}
