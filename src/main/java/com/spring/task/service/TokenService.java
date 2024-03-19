package com.spring.task.service;

import com.spring.task.entity.AuthToken;

import java.util.Optional;

public interface TokenService {
    void saveToken(String token, String refreshToken, String username);
    boolean deleteToken(String token);

    Optional<AuthToken> findByToken(String token);
}
