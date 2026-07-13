package com.iot.history.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "data_points")
public class DataPoint {

    @Id
    private String id;

    private String deviceId;

    private String sensorType;

    private double value;

    private LocalDateTime timestamp;
}
