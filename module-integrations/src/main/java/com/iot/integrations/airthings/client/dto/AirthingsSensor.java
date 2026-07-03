package com.iot.integrations.airthings.client.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AirthingsSensor {
    private String sensorType;
    private Double value;
    private String unit;
}
