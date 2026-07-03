package com.iot.integrations.airthings.client;

import com.iot.integrations.airthings.client.dto.AirthingsDeviceResponse;
import com.iot.integrations.airthings.client.dto.AirthingsSensorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class AirthingsClient {

    private final AirthingsProperties airthingsProperties;
    private final RestTemplate restTemplate;

    public AirthingsClient(AirthingsProperties airthingsProperties, RestTemplate restTemplate) {
        this.airthingsProperties = airthingsProperties;
        this.restTemplate = restTemplate;
    }

    public String getToken() {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "client_credentials");
        params.add("client_id", airthingsProperties.getClientId());
        params.add("client_secret", airthingsProperties.getClientSecret());
        params.add("scope", "read:device:current_values");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request =
                new HttpEntity<>(params, headers);

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                airthingsProperties.getTokenUrl(),
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<Map<String, Object>>() {}
        );

        return Optional.ofNullable(response.getBody())
                .map(body -> (String) body.get("access_token"))
                .orElseThrow(() -> new RuntimeException("Réponse token vide !"));
    }

    public AirthingsDeviceResponse getDevices(String token) {
        HttpEntity<Void> request = createAuthRequest(token);
        ResponseEntity<AirthingsDeviceResponse> response = restTemplate.exchange(
                airthingsProperties.getApiUrl() + "/accounts/" + airthingsProperties.getAccountId() + "/devices",
                HttpMethod.GET,
                request,
                AirthingsDeviceResponse.class
        );
        log.debug("Devices response : {}", response.getBody());
        return response.getBody();
    }

    public AirthingsSensorResponse getSensors(String token) {
        HttpEntity<Void> request = createAuthRequest(token);
        ResponseEntity<AirthingsSensorResponse> response = restTemplate.exchange(
                airthingsProperties.getApiUrl() + "/accounts/" + airthingsProperties.getAccountId() + "/sensors",
                HttpMethod.GET,
                request,
                AirthingsSensorResponse.class
        );
        log.debug("Sensors response : {}", response.getBody());
        return response.getBody();
    }

    private HttpEntity<Void> createAuthRequest(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        return new HttpEntity<>(headers);
    }
}
