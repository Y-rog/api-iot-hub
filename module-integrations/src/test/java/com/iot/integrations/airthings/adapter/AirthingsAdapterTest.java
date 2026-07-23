package com.iot.integrations.airthings.adapter;

import com.iot.integrations.airthings.client.dto.AirthingsDevice;
import com.iot.integrations.airthings.client.dto.AirthingsDeviceResponse;
import com.iot.integrations.airthings.client.dto.AirthingsSensor;
import com.iot.integrations.airthings.client.dto.AirthingsSensorResponse;
import com.iot.integrations.airthings.client.dto.AirthingsSensorResult;
import com.iot.shared.enums.DeviceBrand;
import com.iot.shared.enums.DeviceType;
import com.iot.shared.model.DeviceData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class AirthingsAdapterTest {

    // Pas de vraies dépendances externes à mocker ici :
    // AirthingsAdapter est une classe pure, sans appel réseau ni base de données,
    // elle transforme juste des DTOs en DeviceData. @InjectMocks suffit même sans @Mock.
    @InjectMocks
    private AirthingsAdapter airthingsAdapter;

    @Test
    void should_adapt_devices_with_matching_sensor_data() {
        // GIVEN
        // L'API Airthings renvoie ses données en 2 appels séparés :
        // un pour la liste des appareils, un pour leurs mesures.
        // L'adaptateur doit relier les deux via le serialNumber commun.
        AirthingsDevice device = new AirthingsDevice();
        device.setSerialNumber("2960197472");
        device.setName("View Plus Chambre Clémence");

        AirthingsDeviceResponse devicesResponse = new AirthingsDeviceResponse();
        devicesResponse.setDevices(List.of(device));

        // Deux mesures différentes pour ce même appareil (même serialNumber)
        AirthingsSensor tempSensor = new AirthingsSensor();
        tempSensor.setSensorType("temp");
        tempSensor.setValue(24.6);

        AirthingsSensor co2Sensor = new AirthingsSensor();
        co2Sensor.setSensorType("co2");
        co2Sensor.setValue(460.0);

        AirthingsSensorResult sensorResult = new AirthingsSensorResult();
        sensorResult.setSerialNumber("2960197472");
        sensorResult.setSensors(List.of(tempSensor, co2Sensor));

        AirthingsSensorResponse sensorsResponse = new AirthingsSensorResponse();
        sensorsResponse.setResults(List.of(sensorResult));

        // WHEN
        List<DeviceData> result = airthingsAdapter.adapt(devicesResponse, sensorsResponse);

        // THEN
        // Vérifie que le device et ses 2 mesures ont bien été fusionnés
        // en un seul DeviceData, avec les bonnes métadonnées (type, marque)
        assertThat(result).hasSize(1);

        DeviceData data = result.get(0);
        assertThat(data.getDeviceId()).isEqualTo("2960197472");
        assertThat(data.getName()).isEqualTo("View Plus Chambre Clémence");
        assertThat(data.getType()).isEqualTo(DeviceType.AIR_QUALITY);
        assertThat(data.getSource()).isEqualTo(DeviceBrand.AIRTHINGS);
        assertThat(data.getSensors()).containsEntry("temp", 24.6);
        assertThat(data.getSensors()).containsEntry("co2", 460.0);
    }

    @Test
    void should_return_empty_sensors_when_no_matching_sensor_data_found() {
        // GIVEN
        // Cas limite important en pratique : parfois l'API Airthings répond
        // avec la liste des appareils mais sans encore leurs mesures
        // (capteur qui vient d'être ajouté, ou décalage temporaire entre les 2 appels).
        // L'adaptateur ne doit JAMAIS planter dans ce cas — juste retourner
        // un device avec une liste de capteurs vide, pas une exception.
        AirthingsDevice device = new AirthingsDevice();
        device.setSerialNumber("2960197999");
        device.setName("Capteur Isolé");

        AirthingsDeviceResponse devicesResponse = new AirthingsDeviceResponse();
        devicesResponse.setDevices(List.of(device));

        // Les mesures reçues concernent un AUTRE appareil, aucune correspondance
        AirthingsSensorResult sensorResult = new AirthingsSensorResult();
        sensorResult.setSerialNumber("autre-numero-serie");
        sensorResult.setSensors(List.of());

        AirthingsSensorResponse sensorsResponse = new AirthingsSensorResponse();
        sensorsResponse.setResults(List.of(sensorResult));

        // WHEN
        List<DeviceData> result = airthingsAdapter.adapt(devicesResponse, sensorsResponse);

        // THEN
        // Le device apparaît bien dans le résultat (on ne perd jamais un appareil),
        // simplement sans aucune mesure associée
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getSensors()).isEmpty();
    }

    @Test
    void should_adapt_multiple_devices_independently() {
        // GIVEN
        // Avec 4 vrais capteurs Airthings en production, il faut vérifier
        // qu'aucune mesure ne "fuit" d'un appareil vers un autre —
        // chaque device doit garder strictement ses propres valeurs.
        AirthingsDevice device1 = new AirthingsDevice();
        device1.setSerialNumber("device-1");
        device1.setName("Salon");

        AirthingsDevice device2 = new AirthingsDevice();
        device2.setSerialNumber("device-2");
        device2.setName("Chambre");

        AirthingsDeviceResponse devicesResponse = new AirthingsDeviceResponse();
        devicesResponse.setDevices(List.of(device1, device2));

        // Deux valeurs d'humidité DIFFÉRENTES, une par appareil
        AirthingsSensor humiditySensor1 = new AirthingsSensor();
        humiditySensor1.setSensorType("humidity");
        humiditySensor1.setValue(55.0);

        AirthingsSensorResult result1 = new AirthingsSensorResult();
        result1.setSerialNumber("device-1");
        result1.setSensors(List.of(humiditySensor1));

        AirthingsSensor humiditySensor2 = new AirthingsSensor();
        humiditySensor2.setSensorType("humidity");
        humiditySensor2.setValue(60.0);

        AirthingsSensorResult result2 = new AirthingsSensorResult();
        result2.setSerialNumber("device-2");
        result2.setSensors(List.of(humiditySensor2));

        AirthingsSensorResponse sensorsResponse = new AirthingsSensorResponse();
        sensorsResponse.setResults(List.of(result1, result2));

        // WHEN
        List<DeviceData> result = airthingsAdapter.adapt(devicesResponse, sensorsResponse);

        // THEN
        // Chaque device garde bien sa propre valeur d'humidité,
        // aucun mélange entre 55.0 (Salon) et 60.0 (Chambre)
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getSensors()).containsEntry("humidity", 55.0);
        assertThat(result.get(1).getSensors()).containsEntry("humidity", 60.0);
    }
}