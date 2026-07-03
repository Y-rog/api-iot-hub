package com.iot.api.mapper;

import com.iot.api.dto.DeviceDTO;
import com.iot.devices.model.Device;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DeviceMapper {
    DeviceDTO toDTO (Device device);
    Device toEntity (DeviceDTO dto);

}
