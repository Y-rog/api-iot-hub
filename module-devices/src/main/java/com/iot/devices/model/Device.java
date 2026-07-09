package com.iot.devices.model;

import com.iot.shared.enums.DeviceBrand;
import com.iot.shared.enums.DeviceType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "devices")
public class Device {

    @Id
    private String id;

    private String name;

    @Enumerated(EnumType.STRING)
    private DeviceType type;

    @Enumerated(EnumType.STRING)
    private DeviceBrand brand;

    private boolean connected;

    private LocalDateTime lastSeen;

}
