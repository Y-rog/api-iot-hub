package com.iot.alerts.model;

import jakarta.persistence.*;
import lombok.*;
import com.iot.shared.enums.AlertOperator;

@Entity
@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "alertRules")
public class AlertRule {

    @Id
    private String id;

    private String sensorType;

    @Enumerated(EnumType.STRING)
    private AlertOperator operator;

    private Double threshold;

    private String message;

    private String deviceId;

}
