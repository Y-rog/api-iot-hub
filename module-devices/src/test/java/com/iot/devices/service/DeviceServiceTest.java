package com.iot.devices.service;

import com.iot.devices.model.Device;
import com.iot.devices.port.out.DeviceRepository;
import com.iot.shared.enums.DeviceBrand;
import com.iot.shared.enums.DeviceType;
import com.iot.shared.event.DeviceDataEvent;
import com.iot.shared.exception.DeviceNotFoundException;
import com.iot.shared.model.DeviceData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeviceServiceTest {

    @Mock
    private DeviceRepository deviceRepository;

    @InjectMocks
    private DeviceService deviceService;

    @Test
    void should_return_device_when_it_exists() {
        // Cas normal : le repository trouve bien le device demandé
        Device device = Device.builder()
                .id("device-1")
                .name("Thermostat Salon")
                .build();

        when(deviceRepository.findById("device-1")).thenReturn(Optional.of(device));

        Device result = deviceService.getDevice("device-1");

        assertThat(result.getId()).isEqualTo("device-1");
        assertThat(result.getName()).isEqualTo("Thermostat Salon");
    }

    @Test
    void should_throw_exception_when_device_does_not_exist() {
        // Un ID inconnu doit lever une exception claire, jamais retourner null silencieusement
        when(deviceRepository.findById("inconnu")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> deviceService.getDevice("inconnu"))
                .isInstanceOf(DeviceNotFoundException.class);
    }

    @Test
    void should_return_all_devices() {
        // Vérifie que la liste complète des devices est bien renvoyée telle quelle
        Device device1 = Device.builder().id("device-1").name("Salon").build();
        Device device2 = Device.builder().id("device-2").name("Chambre").build();

        when(deviceRepository.findAll()).thenReturn(List.of(device1, device2));

        List<Device> result = deviceService.getAllDevices();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(Device::getName).containsExactly("Salon", "Chambre");
    }

    @Test
    void should_create_device_and_delegate_to_repository() {
        // Vérifie que createDevice délègue bien la sauvegarde au repository
        Device newDevice = Device.builder().id("device-3").name("Nouveau capteur").build();

        when(deviceRepository.saveDevice(newDevice)).thenReturn(newDevice);

        Device result = deviceService.createDevice(newDevice);

        assertThat(result).isEqualTo(newDevice);
        verify(deviceRepository, times(1)).saveDevice(newDevice);
    }

    @Test
    void should_delete_device_by_id() {
        // Vérifie que la suppression appelle bien le repository avec le bon ID
        deviceService.deleteDevice("device-1");

        verify(deviceRepository, times(1)).deleteDevice("device-1");
    }

    @Test
    void should_create_or_update_device_when_data_event_received() {
        // C'est ce mécanisme qui garde la liste des devices à jour : à chaque
        // mesure reçue (Airthings ou Sinopé), le device est marqué connecté
        // avec un nouveau timestamp de dernière activité
        DeviceData data = DeviceData.builder()
                .deviceId("device-4")
                .name("View Plus Sous-sol")
                .type(DeviceType.AIR_QUALITY)
                .source(DeviceBrand.AIRTHINGS)
                .build();

        DeviceDataEvent event = DeviceDataEvent.builder()
                .deviceData(data)
                .occurredAt(LocalDateTime.now())
                .build();

        deviceService.onDeviceData(event);

        ArgumentCaptor<Device> deviceCaptor = ArgumentCaptor.forClass(Device.class);
        verify(deviceRepository).saveDevice(deviceCaptor.capture());

        Device savedDevice = deviceCaptor.getValue();
        assertThat(savedDevice.getId()).isEqualTo("device-4");
        assertThat(savedDevice.getName()).isEqualTo("View Plus Sous-sol");
        assertThat(savedDevice.getType()).isEqualTo(DeviceType.AIR_QUALITY);
        assertThat(savedDevice.getBrand()).isEqualTo(DeviceBrand.AIRTHINGS);
        assertThat(savedDevice.isConnected()).isTrue();
        assertThat(savedDevice.getLastSeen()).isNotNull();
    }
}