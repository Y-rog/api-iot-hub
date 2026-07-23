package com.iot.auth.service;

import com.iot.auth.model.User;
import com.iot.auth.port.out.UserRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    void should_return_token_when_credentials_are_valid() {
        // Cas normal : email connu, mot de passe correspondant → un vrai token JWT
        User user = new User(UUID.randomUUID(), "test@test.com", "hashed-password", "USER");

        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("motdepasse", "hashed-password")).thenReturn(true);
        when(jwtService.generateToken("test@test.com")).thenReturn("un.faux.token");

        String token = authService.login("test@test.com", "motdepasse");

        assertThat(token).isEqualTo("un.faux.token");
    }

    @Test
    void should_throw_exception_when_email_does_not_exist() {
        // Sécurité : email inconnu → exception, jamais de token généré
        when(userRepository.findByEmail("inconnu@test.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login("inconnu@test.com", "peuimporte"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Identifiants invalides");

        // Aucun token ne doit jamais être généré si l'email n'existe pas
        verify(jwtService, never()).generateToken(anyString());
    }

    @Test
    void should_throw_exception_when_password_is_incorrect() {
        // Sécurité : mauvais mot de passe → même exception générique que
        // "email inconnu", pour ne jamais révéler côté client lequel des deux
        // était faux (bonne pratique de sécurité)
        User user = new User(UUID.randomUUID(), "test@test.com", "hashed-password", "USER");

        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("mauvais-mdp", "hashed-password")).thenReturn(false);

        assertThatThrownBy(() -> authService.login("test@test.com", "mauvais-mdp"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Identifiants invalides");

        verify(jwtService, never()).generateToken(anyString());
    }

    @Test
    void should_never_call_password_encoder_when_email_is_unknown() {
        // Optimisation/sécurité : si l'email n'existe même pas, inutile de
        // comparer un mot de passe — l'exception doit être levée avant
        when(userRepository.findByEmail("inconnu@test.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login("inconnu@test.com", "peuimporte"))
                .isInstanceOf(IllegalArgumentException.class);

        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }
}
