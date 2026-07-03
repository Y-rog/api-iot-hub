package com.iot.api.dto;

import com.iot.shared.enums.DeviceBrand;
import com.iot.shared.enums.DeviceType;

import java.time.LocalDateTime;

public record DeviceDTO(

   String id,

    String name,

    DeviceType type,

    DeviceBrand brand,

    boolean connected,

    LocalDateTime lastSeen
) {}
