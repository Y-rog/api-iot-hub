package com.iot.devices.port.out;

import com.iot.devices.model.Device;

import java.util.List;
import java.util.Optional;

public interface DeviceRepository{

    Device saveDevice(Device device);

    Optional<Device> findById(String id);

    List<Device> findAll();

    void deleteDevice(String id);

}
