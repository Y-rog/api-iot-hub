package com.iot.alerts.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "alerts")
public class Alert {

    @Id
    private String id;

    private String deviceId;

    private String sensorType;

    private String message;

    private Double value;

    private LocalDateTime createdAt;

    private boolean read;

}
