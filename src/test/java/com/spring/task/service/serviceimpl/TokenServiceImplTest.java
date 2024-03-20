package com.spring.task.service.serviceimpl;

import com.spring.task.entity.AuthToken;
import com.spring.task.exception.ResourceNotFoundException;
import com.spring.task.repository.TokenRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TokenServiceImplTest {

    @Mock
    private TokenRepository tokenRepository;

    @InjectMocks
    private TokenServiceImpl tokenService;

    @Test
    public void testSaveToken() {
        String token = "testToken";
        String refreshToken = "testRefreshToken";
        String username = "testUser";

        AuthToken authToken = new AuthToken();
        authToken.setToken(token);
        authToken.setRefreshToken(refreshToken);
        authToken.setUsername(username);

        when(tokenRepository.findByUsername(username)).thenReturn(Optional.of(authToken));

        tokenService.saveToken(token, refreshToken, username);

        verify(tokenRepository).save(authToken);
    }

    @Test
    public void testDeleteToken_Success() {
        String token = "testToken";
        AuthToken authToken = new AuthToken();
        authToken.setToken(token);

        when(tokenRepository.findByToken(token)).thenReturn(Optional.of(authToken));

        boolean result = tokenService.deleteToken(token);

        assertTrue(result);
        verify(tokenRepository).delete(authToken);

    }

    @Test
    public void testDeleteToken_NotFound() {
        String token = "testToken";

        when(tokenRepository.findByToken(token)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> tokenService.deleteToken(token));
    }

    @Test
    public void testFindByToken_Success() {
        String token = "testToken";
        AuthToken authToken = new AuthToken();
        authToken.setToken(token);

        when(tokenRepository.findByToken(token)).thenReturn(Optional.of(authToken));

        Optional<AuthToken> result = tokenService.findByToken(token);

        assertTrue(result.isPresent());
        assertEquals(authToken, result.get());
        verify(tokenRepository).findByToken(token);
    }

    @Test
    public void testFindByToken_NotFound() {
        String token = "testToken";

        when(tokenRepository.findByToken(token)).thenReturn(Optional.empty());

        Optional<AuthToken> result = tokenService.findByToken(token);

        assertFalse(result.isPresent());
        verify(tokenRepository).findByToken(token);
    }
}