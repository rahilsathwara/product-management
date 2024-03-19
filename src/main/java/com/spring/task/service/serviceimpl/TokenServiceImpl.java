package com.spring.task.service.serviceimpl;

import com.spring.task.entity.AuthToken;
import com.spring.task.exception.ResourceNotFoundException;
import com.spring.task.repository.TokenRepository;
import com.spring.task.service.TokenService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class TokenServiceImpl implements TokenService {
    private static final Logger logger = LoggerFactory.getLogger(TokenServiceImpl.class);

    private final TokenRepository tokenRepository;

    @Override
    @Transactional
    public void saveToken(String token, String refreshToken, String username) {
        AuthToken authToken = tokenRepository.findByUsername(username).orElse(new AuthToken());
        authToken.setToken(token);
        authToken.setRefreshToken(refreshToken);
        authToken.setUsername(username);

        tokenRepository.save(authToken);
        logger.info("Token saved for user: {}", username);
    }

    @Override
    @Transactional
    public boolean deleteToken(String token) {
        AuthToken authToken = tokenRepository.findByToken(token).orElseThrow(()-> {
            logger.error("Token not found");
            return new ResourceNotFoundException("Token not found");
        });

        tokenRepository.delete(authToken);
        logger.info("Token deleted");
        return Boolean.TRUE;
    }

    @Override
    public Optional<AuthToken> findByToken(String token) {
        return tokenRepository.findByToken(token);
    }
}
