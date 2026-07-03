package com.iot.integrations.airthings.client.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class AirthingsSensorResult {
    private String serialNumber;
    private List<AirthingsSensor> sensors;
    private String recorded;
    private int batteryPercentage;
}
