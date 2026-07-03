package com.iot.devices.adapter.out;


import com.iot.devices.model.Device;
import com.iot.devices.port.out.DeviceRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class PostgresDeviceRepository implements DeviceRepository {

    private final DeviceJpaRepository jpa;

    public PostgresDeviceRepository(DeviceJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Device saveDevice(Device device) {
        jpa.save(device);
        return device;
    }

    @Override
    public Optional<Device> findById(String id) {
        return jpa.findById(id);

    }

    @Override
    public List<Device> findAll() {
        return jpa.findAll();
    }

    @Override
    public void deleteDevice(String id) {
        jpa.deleteById(id);

    }
}
