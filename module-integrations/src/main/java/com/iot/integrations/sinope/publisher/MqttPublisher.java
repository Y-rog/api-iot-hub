package com.iot.integrations.sinope.publisher;

import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MqttPublisher {

    // Handler pour envoyer des messages MQTT
    private final MqttPahoMessageHandler mqttOutbound;

    public MqttPublisher(MqttPahoMessageHandler mqttOutbound) {
        this.mqttOutbound = mqttOutbound;
    }

    public void setTemperature(String deviceId, double temperature) {
        // Topic : zigbee2mqtt/thermostat-chambre-ethan/set
        String topic = "zigbee2mqtt/" + deviceId + "/set";

        // Payload JSON pour changer la température cible
        String payload = "{\"occupied_heating_setpoint\": " + temperature + "}";

        log.info("Envoi température {} °C sur topic {}", temperature, topic);

        // Envoie le message MQTT avec le bon topic
        mqttOutbound.handleMessage(
                MessageBuilder.withPayload(payload)
                        .setHeader(MqttHeaders.TOPIC, topic)
                        .build()
        );
    }
}
