package com.iot.api.dto;

import com.iot.shared.enums.AlertOperator;

public record AlertRuleDTO (

        String sensorType,

        AlertOperator operator,

        Double threshold,

        String message,

        String deviceId

) {}
