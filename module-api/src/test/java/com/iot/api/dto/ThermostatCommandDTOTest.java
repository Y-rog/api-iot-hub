package com.iot.api.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ThermostatCommandDTOTest {

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void tearDown() {
        factory.close();
    }

    @Test
    void should_have_no_violations_when_temperature_is_within_valid_range() {
        // Une valeur normale, en plein milieu de la plage autorisée
        ThermostatCommandDTO dto = new ThermostatCommandDTO(20.0);

        Set<ConstraintViolation<ThermostatCommandDTO>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }

    @Test
    void should_have_no_violations_for_the_exact_minimum_boundary() {
        // 7.0 est la limite basse elle-même — doit être acceptée, pas juste "au-dessus"
        ThermostatCommandDTO dto = new ThermostatCommandDTO(7.0);

        Set<ConstraintViolation<ThermostatCommandDTO>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }

    @Test
    void should_have_no_violations_for_the_exact_maximum_boundary() {
        // 22.0 est la limite haute elle-même — même logique que le test précédent
        ThermostatCommandDTO dto = new ThermostatCommandDTO(22.0);

        Set<ConstraintViolation<ThermostatCommandDTO>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }

    @Test
    void should_reject_temperature_below_minimum() {
        // C'est exactement ce genre de valeur (froid extrême) que la validation
        // doit bloquer avant qu'elle n'atteigne le vrai thermostat physique
        ThermostatCommandDTO dto = new ThermostatCommandDTO(5.0);

        Set<ConstraintViolation<ThermostatCommandDTO>> violations = validator.validate(dto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("La température minimale est 7°C");
    }

    @Test
    void should_reject_temperature_above_maximum() {
        ThermostatCommandDTO dto = new ThermostatCommandDTO(30.0);

        Set<ConstraintViolation<ThermostatCommandDTO>> violations = validator.validate(dto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("La température maximale est 22°C");
    }

    @Test
    void should_reject_null_temperature() {
        // Une requête sans température du tout doit être bloquée explicitement,
        // pas juste échouer plus loin dans le code avec une NullPointerException
        ThermostatCommandDTO dto = new ThermostatCommandDTO(null);

        Set<ConstraintViolation<ThermostatCommandDTO>> violations = validator.validate(dto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("La température est obligatoire");
    }
}
