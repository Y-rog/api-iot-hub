package com.iot.api.dto;

import java.time.LocalDateTime;

public record DataPointDTO (

    String deviceId,

    String sensorType,

    double value,

    LocalDateTime timestamp

) {}
