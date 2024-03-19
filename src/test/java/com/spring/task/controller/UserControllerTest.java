package com.spring.task.controller;

import com.spring.task.entity.User;
import com.spring.task.exception.UnauthorizedException;
import com.spring.task.payload.request.AuthRequest;
import com.spring.task.payload.request.UserRequest;
import com.spring.task.payload.response.CustomUserDetails;
import com.spring.task.payload.response.JwtResponse;
import com.spring.task.payload.response.UserResponse;
import com.spring.task.service.UserService;
import com.spring.task.web.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private UserService userService;
    @InjectMocks
    private UserController userController;

    @Test
    public void testSaveUser() {
        UserRequest userRequest = new UserRequest();
        User savedUser = new User();

        when(userService.createUser(userRequest)).thenReturn(savedUser);
        when(userService.mapEntityToResponse(savedUser)).thenReturn(new UserResponse());
        ResponseEntity<ApiResponse> response = userController.saveUser(userRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("User created successfully", response.getBody().getMessage());
        assertEquals(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), response.getBody().getTimestamp().truncatedTo(ChronoUnit.SECONDS));
    }

    @Test
    public void testAuthenticateAndGetToken() {
        AuthRequest authRequest = new AuthRequest();
        JwtResponse jwtResponse = new JwtResponse();

        when(userService.authenticate(authRequest)).thenReturn(jwtResponse);

        ResponseEntity<?> response = userController.authenticateAndGetToken(authRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(jwtResponse, response.getBody());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetAllUsers() {
        List<UserResponse> userResponseList = new ArrayList<>();

        when(userService.getAllUsers()).thenReturn(userResponseList);
        ResponseEntity<ApiResponse> response = userController.getAllUsers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Get all roles", response.getBody().getMessage());
        assertEquals(userResponseList, response.getBody().getData());
        assertEquals(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), response.getBody().getTimestamp().truncatedTo(ChronoUnit.SECONDS));
    }

    @Test
    public void testGetCurrentUserProfile_Authenticated() {
        // Given
        String username = "testuser"; // Replace with a valid username for the test
        User user = mock(User.class);
        when(user.getEmail()).thenReturn(username);

        CustomUserDetails userDetails = new CustomUserDetails(user);

        UserResponse currentUserProfile = new UserResponse();
        // Set up the currentUserProfile with necessary data

        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(userService.getCurrentUserProfile(username)).thenReturn(currentUserProfile);

        // When
        ResponseEntity<ApiResponse> response = userController.getCurrentUserProfile();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Get logged in user profile", response.getBody().getMessage());
        assertEquals(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), response.getBody().getTimestamp().truncatedTo(ChronoUnit.SECONDS));
    }

    @Test
    public void testGetCurrentUserProfile_NotAuthenticated() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(false);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> userController.getCurrentUserProfile());
        assertEquals("User not authenticated", exception.getMessage());
    }

    @Test
    public void testLogoutUser() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        String logoutMessage = "Logout successful";
        when(userService.logoutUser(request, response)).thenReturn(logoutMessage);

        ResponseEntity<ApiResponse> responseEntity = userController.logoutUser(request, response);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("SUCCESS", responseEntity.getBody().getMessage());
        assertEquals(logoutMessage, responseEntity.getBody().getData());
        assertEquals(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), responseEntity.getBody().getTimestamp().truncatedTo(ChronoUnit.SECONDS));
    }
}
