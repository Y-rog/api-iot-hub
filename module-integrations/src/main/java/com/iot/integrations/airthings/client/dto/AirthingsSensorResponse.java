package com.iot.integrations.airthings.client.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class AirthingsSensorResponse {
    private List<AirthingsSensorResult> results;
    private boolean hasNext;
    private int totalPages;
}