package com.iot.integrations.sinope.config;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;

@Configuration
public class MqttConfig {

    private final SinopeProperties sinopeProperties;

    public MqttConfig(SinopeProperties sinopeProperties) {
        this.sinopeProperties = sinopeProperties;
    }

    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        // Crée la factory de connexion au broker Mosquitto
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        MqttConnectOptions options = new MqttConnectOptions();

        // URL du broker Mosquitto sur le Raspberry Pi
        options.setServerURIs(new String[]{sinopeProperties.getBrokerUrl()});

        // Nouvelle session à chaque connexion
        options.setCleanSession(true);

        // Reconnexion automatique si coupure réseau
        options.setAutomaticReconnect(true);

        factory.setConnectionOptions(options);
        return factory;
    }

    @Bean
    public MqttPahoMessageHandler mqttOutbound() {
        // Handler pour ENVOYER des messages MQTT
        // "sinope-publisher" = identifiant unique du client MQTT
        MqttPahoMessageHandler handler =
                new MqttPahoMessageHandler("sinope-publisher", mqttClientFactory());

        // Envoi asynchrone → pas de blocage
        handler.setAsync(true);

        // QoS 1 = garantit la livraison du message
        handler.setDefaultQos(1);

        return handler;
    }
}
