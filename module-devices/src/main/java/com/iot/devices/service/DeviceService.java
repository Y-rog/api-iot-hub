package com.iot.devices.service;

import com.iot.devices.model.Device;
import com.iot.devices.port.in.DeviceUseCase;
import com.iot.devices.port.out.DeviceRepository;
import com.iot.shared.enums.DeviceType;
import com.iot.shared.event.DeviceDataEvent;
import com.iot.shared.exception.DeviceNotFoundException;
import com.iot.shared.model.DeviceData;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
public class DeviceService implements DeviceUseCase {

    private final DeviceRepository deviceRepository;

    public DeviceService(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    @Override
    public Device createDevice(Device device) {
       return deviceRepository.saveDevice(device);

    }

    @Override
    public Device getDevice(String id) {
        return deviceRepository.findById(id)
                .orElseThrow(() -> new DeviceNotFoundException(id));

    }

    @Override
    public List<Device> getAllDevices() {
        return deviceRepository.findAll();
    }


    @Override
    public Device updateDevice(Device device) {
        return deviceRepository.saveDevice(device);

    }

    @Override
    public void deleteDevice(String id) {
        deviceRepository.deleteDevice(id);

    }

    @EventListener
    public void onDeviceData(DeviceDataEvent event) {
        DeviceData data = event.getDeviceData();
        Device device = Device.builder()
                .id(data.getDeviceId())
                .name(data.getName())
                .brand(data.getSource())
                .type(DeviceType.valueOf(data.getType()))
                .connected(true)
                .lastSeen(LocalDateTime.now())
                .build();
        deviceRepository.saveDevice(device);
    }
}
