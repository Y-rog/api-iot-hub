package com.iot.integrations.sinope.adapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iot.shared.enums.DeviceBrand;
import com.iot.shared.enums.DeviceType;
import com.iot.shared.model.DeviceData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SinopeAdapterTest {

    private SinopeAdapter sinopeAdapter;

    @BeforeEach
    void setUp() {
        // Un vrai ObjectMapper suffit ici — c'est juste du parsing JSON standard,
        // aucune raison de le mocker
        sinopeAdapter = new SinopeAdapter(new ObjectMapper());
    }

    @Test
    void should_adapt_valid_mqtt_payload_correctly() {
        // GIVEN
        // Un vrai message MQTT tel qu'envoyé par Zigbee2MQTT pour un thermostat Sinopé
        String topic = "zigbee2mqtt/thermostat-salon";
        String payload = """
                {
                    "local_temperature": 21.5,
                    "power": 150,
                    "voltage": 244.5,
                    "current": 0.6,
                    "energy": 12.3
                }
                """;

        // WHEN
        DeviceData result = sinopeAdapter.adapt(topic, payload);

        // THEN
        // Le préfixe "zigbee2mqtt/" doit être retiré pour obtenir le nom réel du device
        assertThat(result).isNotNull();
        assertThat(result.getDeviceId()).isEqualTo("thermostat-salon");
        assertThat(result.getName()).isEqualTo("thermostat-salon");
        assertThat(result.getType()).isEqualTo(DeviceType.THERMOSTAT);
        assertThat(result.getSource()).isEqualTo(DeviceBrand.SINOPE);
        assertThat(result.getSensors()).containsEntry("temperature", 21.5);
        assertThat(result.getSensors()).containsEntry("power", 150);
        assertThat(result.getSensors()).containsEntry("voltage", 244.5);
    }

    @Test
    void should_return_null_when_payload_is_invalid_json() {
        // GIVEN
        // Message MQTT corrompu ou mal formé — arrive parfois en pratique
        // (coupure réseau en plein envoi, firmware bogué du thermostat...)
        String topic = "zigbee2mqtt/thermostat-salon";
        String invalidPayload = "{ceci n'est pas du JSON valide";

        // WHEN
        DeviceData result = sinopeAdapter.adapt(topic, invalidPayload);

        // THEN
        // L'adaptateur ne doit jamais planter toute l'application sur un message
        // corrompu — il retourne simplement null, l'appelant (MqttListener) sait
        // déjà gérer ce cas (if (data != null) { ... })
        assertThat(result).isNull();
    }

    @Test
    void should_extract_device_name_from_topic_for_different_rooms() {
        // GIVEN
        // Vérifie que l'extraction du nom fonctionne pour n'importe quelle pièce,
        // pas seulement un cas particulier testé une seule fois
        String topic = "zigbee2mqtt/thermostat-chambre-parentale";
        String payload = """
                {"local_temperature": 25.0}
                """;

        // WHEN
        DeviceData result = sinopeAdapter.adapt(topic, payload);

        // THEN
        assertThat(result.getDeviceId()).isEqualTo("thermostat-chambre-parentale");
        assertThat(result.getName()).isEqualTo("thermostat-chambre-parentale");
    }

    @Test
    void should_handle_missing_optional_fields_gracefully() {
        // GIVEN
        // Certains messages MQTT n'incluent pas toutes les mesures
        // (ex: "power" absent si le thermostat est en veille) —
        // le payload ne contient que la température
        String topic = "zigbee2mqtt/thermostat-sous-sol";
        String payload = """
                {"local_temperature": 19.8}
                """;

        // WHEN
        DeviceData result = sinopeAdapter.adapt(topic, payload);

        // THEN
        // La température est bien récupérée, les champs absents deviennent null
        // dans le Map plutôt que de faire planter l'adaptateur
        assertThat(result).isNotNull();
        assertThat(result.getSensors().get("temperature")).isEqualTo(19.8);
        assertThat(result.getSensors().get("power")).isNull();
    }
}
