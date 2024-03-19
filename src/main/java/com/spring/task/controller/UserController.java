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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controller class for handling user-related endpoints.
 * Provides APIs for managing users such as creating, updating, deleting, and retrieving users.
 *
 * @author Rahil Sathwara
 * @since 2024-03-20
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/")
public class UserController {

    private final UserService userService;

    /**
     * Registers a new user with the provided user details.
     *
     * @param userRequest The request body containing the user details.
     * @return A ResponseEntity containing the ApiResponse with information about the newly created user.
     */
    @PostMapping("registerUser")
    public ResponseEntity<ApiResponse> saveUser(@Valid @RequestBody UserRequest userRequest) {
        User savedUser = userService.createUser(userRequest);

        return new ResponseEntity<>(new ApiResponse(LocalDateTime.now(), HttpStatus.CREATED, "User created successfully", userService.mapEntityToResponse(savedUser)), HttpStatus.CREATED);
    }

    /**
     * Registers a new user with the provided user details.
     *
     * @param authRequest The request body containing the user details.
     * @return A ResponseEntity containing the ApiResponse with information about the newly created user.
     */
    @PostMapping("authenticate")
    public ResponseEntity<?> authenticateAndGetToken(@Valid @RequestBody AuthRequest authRequest) {
        JwtResponse userToken = userService.authenticate(authRequest);

        return ResponseEntity.ok(userToken);
    }

    /**
     * Retrieves a list of all users.
     *
     * @return A ResponseEntity containing the list of user responses.
     */
    @GetMapping("/users")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<ApiResponse> getAllUsers() {
        List<UserResponse> userResponses = userService.getAllUsers();

        return new ResponseEntity<>(new ApiResponse(LocalDateTime.now(), HttpStatus.OK, "Get all roles", userResponses), HttpStatus.OK);
    }

    /**
     * Retrieves the profile of the currently authenticated user.
     *
     * @return A ResponseEntity containing the user's profile information.
     * @throws UnauthorizedException if the user is not authenticated.
     */
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse> getCurrentUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("User not authenticated");
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UserResponse currentUserProfile = userService.getCurrentUserProfile(userDetails.getUsername());

        return new ResponseEntity<>(new ApiResponse(LocalDateTime.now(), HttpStatus.OK, "Get logged in user profile", currentUserProfile), HttpStatus.OK);
    }

    /**
     * Logout the current user by invalidating the current session and removing the authentication token.
     *
     * @param request  the HTTP request
     * @param response the HTTP response
     * @return a ResponseEntity containing the result of the logout operation
     * @throws Exception if an error occurs during the logout process
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logoutUser(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String msg = userService.logoutUser(request, response);

        return new ResponseEntity<>(new ApiResponse(LocalDateTime.now(), HttpStatus.OK, "SUCCESS", msg), HttpStatus.OK);
    }
}