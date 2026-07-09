package com.iot.alerts.adapter.out;

import com.iot.alerts.model.AlertRule;
import com.iot.alerts.port.out.AlertRuleRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class PostgresAlertRuleRepository implements AlertRuleRepository {

    private final AlertRuleJpaRepository alertRuleJpaRepository;

    public PostgresAlertRuleRepository(AlertRuleJpaRepository alertRuleJpaRepository) {
        this.alertRuleJpaRepository = alertRuleJpaRepository;
    }


    @Override
    public AlertRule saveAlertRule(AlertRule alertRule) {
        return alertRuleJpaRepository.save(alertRule);
    }

    @Override
    public Optional<AlertRule> findById(String id) {
        return alertRuleJpaRepository.findById(id);
    }

    @Override
    public List<AlertRule> findAll() {
        return alertRuleJpaRepository.findAll();
    }

    @Override
    public void deleteAlertRule(String id) {
        alertRuleJpaRepository.deleteById(id);
    }
}
