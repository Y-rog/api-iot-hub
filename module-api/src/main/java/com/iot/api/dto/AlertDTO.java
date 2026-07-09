package com.iot.api.dto;

import java.time.LocalDateTime;

public record AlertDTO(

        String deviceId,

        String sensorType,

        String message,

        Double value,

        LocalDateTime createdAt,

        boolean read
) {}
