package com.iot.shared.event;

import com.iot.shared.model.DeviceData;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class DeviceDataEvent {

    private DeviceData deviceData;
    private LocalDateTime occurredAt;

}
