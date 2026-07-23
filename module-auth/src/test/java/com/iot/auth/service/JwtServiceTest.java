package com.iot.auth.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    // Clé de test suffisamment longue pour HS256 (minimum 256 bits requis)
    private static final String TEST_SECRET = "ceci-est-une-cle-de-test-suffisamment-longue-pour-hs256-123456";

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        // Pas besoin du contexte Spring ici — JwtService est une classe simple
        // qu'on peut instancier directement avec ses paramètres
        jwtService = new JwtService(TEST_SECRET, 86400000L); // 24h
    }

    @Test
    void should_generate_a_non_null_token() {
        // Vérifie juste qu'un token est bien produit, sans erreur
        String token = jwtService.generateToken("test@test.com");

        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
    }

    @Test
    void should_extract_the_correct_email_from_a_valid_token() {
        // Vérifie que l'email encodé dans le token est bien celui qu'on récupère,
        // pas un autre — c'est ce qui identifie l'utilisateur à chaque requête
        String token = jwtService.generateToken("greg@iot-hub.com");

        String extractedEmail = jwtService.extractEmail(token);

        assertThat(extractedEmail).isEqualTo("greg@iot-hub.com");
    }

    @Test
    void should_consider_a_freshly_generated_token_as_valid() {
        String token = jwtService.generateToken("test@test.com");

        assertThat(jwtService.isValid(token)).isTrue();
    }

    @Test
    void should_consider_a_malformed_token_as_invalid() {
        // Sécurité : un token corrompu ou fabriqué à la main
        // ne doit jamais être accepté comme valide
        String fakeToken = "ceci.nest.pas.un.vrai.token";

        assertThat(jwtService.isValid(fakeToken)).isFalse();
    }

    @Test
    void should_consider_a_token_signed_with_a_different_secret_as_invalid() {
        // Sécurité critique : un token signé avec une AUTRE clé secrète
        // (par exemple si quelqu'un essaie de forger un faux token)
        // doit être rejeté, même s'il a la bonne structure
        JwtService otherJwtService = new JwtService(
                "une-toute-autre-cle-secrete-completement-differente-98765",
                86400000L
        );
        String tokenFromOtherService = otherJwtService.generateToken("attaquant@test.com");

        assertThat(jwtService.isValid(tokenFromOtherService)).isFalse();
    }

    @Test
    void should_consider_an_expired_token_as_invalid() {
        // Un token dont la durée de validité est écoulée ne doit plus
        // être accepté — on simule ça avec une expiration quasi immédiate
        JwtService shortLivedJwtService = new JwtService(TEST_SECRET, 1L); // 1 ms
        String token = shortLivedJwtService.generateToken("test@test.com");

        // Petite pause pour laisser le temps au token d'expirer réellement
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        assertThat(shortLivedJwtService.isValid(token)).isFalse();
    }
}
