package com.iot.integrations.airthings.client.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class AirthingsDevice {
    private String serialNumber;
    private String home;
    private String name;
    private String type;
    private List<String> sensors;
}
