package com.iot.integrations.sinope.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "sinope")
public class SinopeProperties {
    private String brokerUrl;
    private List<String> topics;
}
