package com.iot.alerts.port.in;

import com.iot.alerts.model.AlertRule;

import java.util.List;

public interface AlertRuleUseCase {

    AlertRule createAlertRule(AlertRule rule);

    AlertRule getAlertRule(String id);

    List<AlertRule> getAllAlertRules();

    AlertRule updateAlertRule(AlertRule rule);

    void deleteAlertRule(String id);

}
