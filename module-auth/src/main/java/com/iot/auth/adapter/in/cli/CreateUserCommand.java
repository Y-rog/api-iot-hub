package com.iot.auth.adapter.in.cli;

import com.iot.auth.domain.User;
import com.iot.auth.port.out.UserRepositoryPort;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Profile("create-user")
public class CreateUserCommand implements CommandLineRunner {

    private final UserRepositoryPort userRepository;
    private final PasswordEncoder passwordEncoder;

    public CreateUserCommand(UserRepositoryPort userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        String email = extractArg(args, "--email");
        String password = extractArg(args, "--password");

        if (email == null || password == null) {
            System.out.println("Usage : --email=... --password=...");
            return;
        }

        String hashed = passwordEncoder.encode(password);
        User user = new User(null, email, hashed, "USER");
        userRepository.save(user);

        System.out.println("Utilisateur créé : " + email);
    }

    private String extractArg(String[] args, String prefix) {
        for (String arg : args) {
            if (arg.startsWith(prefix + "=")) {
                return arg.substring(prefix.length() + 1);
            }
        }
        return null;
    }
}
