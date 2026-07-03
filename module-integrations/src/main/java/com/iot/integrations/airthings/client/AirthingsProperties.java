package com.iot.integrations.airthings.client;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "airthings")
public class AirthingsProperties {
    private String clientId;
    private String clientSecret;
    private String tokenUrl;
    private String apiUrl;
    private String accountId;
}
