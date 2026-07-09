package com.iot.api.controller;

import com.iot.api.dto.DeviceDTO;
import com.iot.api.mapper.DeviceMapper;
import com.iot.devices.model.Device;
import com.iot.devices.port.in.DeviceUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/devices")
public class DeviceController {
    private final DeviceUseCase deviceUseCase;
    private final DeviceMapper deviceMapper;

    public DeviceController (DeviceUseCase deviceUseCase, DeviceMapper deviceMapper){
        this.deviceUseCase = deviceUseCase;
        this.deviceMapper = deviceMapper;
    }

    @GetMapping
    public List<DeviceDTO> getDevices () {
        return deviceUseCase.getAllDevices()
                .stream()
                .map(deviceMapper::toDTO)
                .toList();
    }

    @GetMapping("{id}")
    public DeviceDTO getDevice(@PathVariable String id) {
        Device device = deviceUseCase.getDevice(id);
        return deviceMapper.toDTO(device);
    }

    @PostMapping
    public ResponseEntity<DeviceDTO>  createDevice(@RequestBody DeviceDTO dto) {
        Device created = deviceUseCase.createDevice(deviceMapper.toEntity(dto));
        return ResponseEntity.created(URI.create("/api/devices/" + created.getId()))
                .body(deviceMapper.toDTO(created));
    }

    @PutMapping("{id}")
    public ResponseEntity<DeviceDTO> updateDevice(@PathVariable String id, @RequestBody DeviceDTO dto) {
        Device device = deviceMapper.toEntity(dto)
                .toBuilder()
                .id(id)
                .build();
        Device updated = deviceUseCase.updateDevice(device);
        return ResponseEntity.ok()
                .location(URI.create("/api/devices/" + updated.getId()))
                .body(deviceMapper.toDTO(updated));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteDevice(@PathVariable String id) {
        deviceUseCase.deleteDevice(id);
        return ResponseEntity.noContent().build();

    }

}
