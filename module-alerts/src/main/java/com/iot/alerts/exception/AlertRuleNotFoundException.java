package com.iot.alerts.exception;

public class AlertRuleNotFoundException extends RuntimeException{

    public AlertRuleNotFoundException(String alertRuleId) {
        super("Règle introuvable : " + alertRuleId);

    }

}
