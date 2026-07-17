package com.iot.alerts.service;

import com.iot.alerts.model.AlertRule;
import com.iot.alerts.port.out.AlertRepository;
import com.iot.alerts.port.out.AlertRuleRepository;
import com.iot.alerts.port.out.NotificationPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AlertServiceTest {

    @Mock
    private AlertRepository alertRepository;

    @Mock
    private AlertRuleRepository alertRuleRepository;

    @Mock
    private NotificationPort notificationPort;

    @InjectMocks
    private AlertService alertService;

    @Test
    void should_create_alert_when_co2_exceeds_threshold() {
        AlertRule rule = AlertRule.builder()
                .sensorType("co2")
                .operator(com.iot.shared.enums.AlertOperator.GREATER_THAN)
                .threshold(1000.0)        // ← seuil
                .message("CO2 trop élevé !")
                .build();
        // WHEN  → appelle la méthode à tester
        // THEN  → vérifie le résultat
    }

}
