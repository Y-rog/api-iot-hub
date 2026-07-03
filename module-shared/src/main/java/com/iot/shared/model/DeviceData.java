package com.iot.shared.model;

import com.iot.shared.enums.DeviceBrand;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Builder
public class DeviceData {

    private String deviceId;

    private String name;

    private String type;

    private Map<String, Object> sensors;

    private DeviceBrand source;

    private LocalDateTime timestamp;

}
