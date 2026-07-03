package com.iot.devices.port.in;


import com.iot.devices.model.Device;

import java.util.List;

public interface DeviceUseCase {

    Device createDevice(Device device);

    Device getDevice(String id);

    List<Device> getAllDevices();

    Device updateDevice(Device device);

    void deleteDevice(String id);

}


