package com.iot.auth.port.in;

public interface AuthUseCase {
    String login(String email, String rawPassword);
}
