package com.iot.devices.adapter.out;

import com.iot.devices.model.Device;
import org.springframework.data.jpa.repository.JpaRepository;


public interface DeviceJpaRepository extends JpaRepository<Device, String> {}

