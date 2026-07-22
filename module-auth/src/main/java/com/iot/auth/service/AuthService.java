package com.iot.auth.service;

import com.iot.auth.port.in.AuthUseCase;
import com.iot.auth.port.out.UserRepositoryPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthService implements AuthUseCase {

    private final UserRepositoryPort userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepositoryPort userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public String login(String email, String rawPassword) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("🔒 Tentative de connexion échouée — email inconnu : {}", email);
                    return new IllegalArgumentException("Identifiants invalides");
                });

        if (!passwordEncoder.matches(rawPassword, user.getHashedPassword())) {
            log.warn("🔒 Tentative de connexion échouée — mauvais mot de passe pour : {}", email);
            throw new IllegalArgumentException("Identifiants invalides");
        }

        log.info("✅ Connexion réussie : {}", email);
        return jwtService.generateToken(user.getEmail());
    }
}
