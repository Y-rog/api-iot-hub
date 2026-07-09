package com.iot.alerts.port.out;

import com.iot.alerts.model.AlertRule;

import java.util.List;
import java.util.Optional;

public interface AlertRuleRepository {

    AlertRule saveAlertRule(AlertRule alertRule);

    Optional<AlertRule> findById(String id);

    List<AlertRule> findAll();

    void deleteAlertRule(String id);

}
