package com.spring.task.service.serviceimpl;

import com.spring.task.entity.Role;
import com.spring.task.entity.User;
import com.spring.task.exception.CommonException;
import com.spring.task.exception.ResourceNotFoundException;
import com.spring.task.exception.UserAlreadyExistsException;
import com.spring.task.payload.request.AuthRequest;
import com.spring.task.payload.request.UserRequest;
import com.spring.task.payload.response.JwtResponse;
import com.spring.task.payload.response.RoleResponse;
import com.spring.task.payload.response.UserResponse;
import com.spring.task.repository.UserRepository;
import com.spring.task.service.RoleService;
import com.spring.task.service.TokenService;
import com.spring.task.service.UserService;
import com.spring.task.util.CommonUtils;
import com.spring.task.util.JwtUtils;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public User createUser(UserRequest userRequest) {
        logger.info("Creating user: {}", userRequest.getEmail());

        User validatedForm = validateUserRequest(userRequest);
        validatedForm.setCreatedAt(LocalDateTime.now());

        return userRepository.save(validatedForm);
    }

    private User validateUserRequest(UserRequest userRequest) {
        logger.info("Validating user request for email: {}", userRequest.getEmail());

        if (userRequest == null) {
            logger.error("User request cannot be null.");
            throw new IllegalArgumentException("User request cannot be null.");
        }

        // email validate
        userRepository.findByEmail(userRequest.getEmail())
                .ifPresent(user -> {
                    logger.error("There is an existing user with email: " + userRequest.getEmail());
                    throw new UserAlreadyExistsException("There is an existing user with email: " + userRequest.getEmail());
                });

        User user = new User();
        BeanUtils.copyProperties(userRequest, user);

        // password validate
        if (userRequest.getPassword() == null || userRequest.getConfirmPassword() == null) {
            logger.error("Password or Confirm Password cannot be null");
            throw new CommonException("Password or Confirm Password cannot be null");
        } else if (!userRequest.getPassword().equals(userRequest.getConfirmPassword())) {
            logger.error("Password and Confirm Password are not the same");
            throw new CommonException("Password and Confirm Password are not the same");
        } else {
            user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        }

        // role validate
        if(userRequest.getRoles() == null || userRequest.getRoles().isEmpty()) {
            logger.error("Role is empty");
            throw new CommonException("Role is empty");
        } else {
            List<String> appRoles = CommonUtils.fetchAllAppRoles();
            Set<Role> roles = new HashSet<>();
            userRequest.getRoles().forEach(role -> {
                if (appRoles.stream().anyMatch(appRole -> appRole.equalsIgnoreCase(role))) {
                    Role userRole = roleService.findByRoleName(role)
                            .orElseGet(() -> {
                                Role newRole = new Role();
                                newRole.setName(role);
                                newRole.setCreatedAt(LocalDateTime.now());

                                return roleService.createRole(newRole);
                            });
                    roles.add(userRole);
                }
            });
            user.setRoles(roles);
        }
        logger.info("User request validated successfully for email: {}", userRequest.getEmail());
        return user;
    }

    @Override
    public JwtResponse authenticate(AuthRequest authRequest) {
        logger.info("Authenticating user with email: {}", authRequest.getEmail());
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authenticate);
        if (authenticate.isAuthenticated()) {
            String accessToken = jwtUtils.generateToken(authRequest.getEmail());
            String refreshToken = jwtUtils.generateRefreshToken(authRequest.getEmail());
            logger.info("User authenticated successfully with email: {}", authRequest.getEmail());
            // save token
            tokenService.saveToken(accessToken, refreshToken, authRequest.getEmail());
            
            return JwtResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();
        } else {
            logger.error("Authentication failed for user with email: {}", authRequest.getEmail());
            throw new BadCredentialsException("Invalid Authentication");
        }
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream().map(this::mapEntityToResponse).collect(Collectors.toList());
    }

    @Override
    public UserResponse getCurrentUserProfile(String userName) {
        User user = userRepository.findByEmail(userName)
                .orElseThrow(() -> {
                    logger.error("User not found for username: {}", userName);
                    return new ResourceNotFoundException("User not found");
                });
        logger.info("User profile fetched successfully for username: {}", userName);

        return mapEntityToResponse(user);
    }

    @Override
    public String logoutUser(HttpServletRequest request, HttpServletResponse response) {
        String token = request.getHeader("Authorization").substring(7);
        if (!StringUtils.isBlank(token)) {
            if (tokenService.deleteToken(token)) {
                logger.info("User logged out successfully");
                return "Logout successfully";
            } else {
                logger.error("Failed to delete token");
            }
        }
        return null;
    }

    @Override
    public UserResponse mapEntityToResponse(User user) {
        Set<RoleResponse> roleResponses = user.getRoles().stream()
                .map(roleService::mapEntityToResponse).collect(Collectors.toSet());

        return new UserResponse(user.getId(), user.getName(), user.getEmail(), roleResponses, user.getCreatedAt());
    }
}