package com.iot.integrations.sinope.listener;

import com.iot.integrations.sinope.adapter.SinopeAdapter;
import com.iot.integrations.sinope.config.SinopeProperties;
import com.iot.shared.event.DeviceDataEvent;
import com.iot.shared.model.DeviceData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class MqttListener {

    private final MqttPahoClientFactory mqttClientFactory;
    private final SinopeAdapter sinopeAdapter;
    private final ApplicationEventPublisher eventPublisher;
    private final SinopeProperties sinopeProperties;

    public MqttListener(MqttPahoClientFactory mqttClientFactory,
                        SinopeAdapter sinopeAdapter,
                        ApplicationEventPublisher eventPublisher, SinopeProperties sinopeProperties) {
        this.mqttClientFactory = mqttClientFactory;
        this.sinopeAdapter = sinopeAdapter;
        this.eventPublisher = eventPublisher;
        this.sinopeProperties = sinopeProperties;
    }

    @Bean
    public MessageChannel mqttInputChannel() {
        return new DirectChannel();
    }

    @Bean
    public MqttPahoMessageDrivenChannelAdapter mqttInbound(MessageChannel mqttInputChannel) {

        String[] topicsArray = sinopeProperties.getTopics().toArray(new String[0]);

        // Crée l'adaptateur MQTT qui s'abonne aux topics
        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter(
                        "sinope-client",    // identifiant unique du client MQTT
                        mqttClientFactory,  // connexion au broker Mosquitto
                        topicsArray         // topics auxquels s'abonner
                );

        // Timeout de connexion en ms
        adapter.setCompletionTimeout(5000);

        // Convertit les messages MQTT en messages Spring
        adapter.setConverter(new DefaultPahoMessageConverter());

        // QoS 1 = garantit la livraison des messages
        adapter.setQos(1);

        // Canal de sortie → vers le MessageHandler
        adapter.setOutputChannel(mqttInputChannel);

        return adapter;
    }

    @Bean
    @ServiceActivator(inputChannel = "mqttInputChannel") // écoute le canal MQTT
    public MessageHandler mqttMessageHandler() {
        return message -> {
            // Récupère le topic du message (ex: zigbee2mqtt/thermostat-chambre-ethan)
            String topic = message.getHeaders()
                    .get("mqtt_receivedTopic", String.class);

            // Récupère le contenu JSON du message
            String payload = (String) message.getPayload();

            log.debug("Message MQTT reçu sur topic {} : {}", topic, payload);

            // Traduit le JSON Sinopé → DeviceData
            DeviceData data = sinopeAdapter.adapt(topic, payload);

            if (data != null) {
                log.info("Publication event pour thermostat : {}", data.getName());
                // Publie l'event → DeviceService, AlertService, HistoryService l'écoutent !
                eventPublisher.publishEvent(DeviceDataEvent.builder()
                        .deviceData(data)
                        .occurredAt(LocalDateTime.now())
                        .build());
            }
        };
    }
}
