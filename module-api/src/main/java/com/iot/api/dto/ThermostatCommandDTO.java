package com.iot.api.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record ThermostatCommandDTO(

        @NotNull(message = "La température est obligatoire")
        @DecimalMin(value = "7.0", message = "La température minimale est 15°C")
        @DecimalMax(value = "22.0", message = "La température maximale est 22°C")
        Double temperature

) {}