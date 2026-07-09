package com.iot.integrations.airthings.client.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class AirthingsDeviceResponse {
    private List<AirthingsDevice> devices;
}
