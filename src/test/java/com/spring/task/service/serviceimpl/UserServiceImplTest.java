package com.spring.task.service.serviceimpl;


import com.spring.task.entity.Role;
import com.spring.task.entity.User;
import com.spring.task.exception.ResourceNotFoundException;
import com.spring.task.payload.request.AuthRequest;
import com.spring.task.payload.request.UserRequest;
import com.spring.task.payload.response.JwtResponse;
import com.spring.task.payload.response.UserResponse;
import com.spring.task.repository.RoleRepository;
import com.spring.task.repository.UserRepository;
import com.spring.task.service.RoleService;
import com.spring.task.service.TokenService;
import com.spring.task.util.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleService roleService;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtUtils jwtUtils;
    @Mock
    private TokenService tokenService;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    public void testCreateUser_Success() {
        UserRequest userRequest = new UserRequest();
        Set<String> roles = new HashSet<>();
        roles.add("ROLE_USER");

        userRequest.setEmail("test@example.com");
        userRequest.setPassword("password"); // Set a non-null password
        userRequest.setConfirmPassword("password"); // Set a non-null confirmPassword
        userRequest.setRoles(roles);

        User user = new User();
        user.setEmail(userRequest.getEmail());
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        user.setCreatedAt(LocalDateTime.now());

        when(userRepository.save(any(User.class))).thenReturn(user);

        User createdUser = userService.createUser(userRequest);

        assertNotNull(createdUser);
        assertEquals(userRequest.getEmail(), createdUser.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    public void testValidateUserRequest_Success() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        UserRequest userRequest = new UserRequest();
        Set<String> roles = new HashSet<>();
        roles.add("ROLE_USER");

        userRequest.setEmail("test@example.com");
        userRequest.setPassword("password");
        userRequest.setConfirmPassword("password");

        userRequest.setRoles(roles);

        when(userRepository.findByEmail(userRequest.getEmail())).thenReturn(Optional.empty());
        when(roleService.findByRoleName(anyString())).thenReturn(Optional.empty());
        when(roleService.createRole(any(Role.class))).thenReturn(new Role());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");


        Method method = UserServiceImpl.class.getDeclaredMethod("validateUserRequest", UserRequest.class);
        method.setAccessible(true);

        User validatedUser = (User) method.invoke(userService, userRequest);

        assertNotNull(validatedUser);
        assertEquals(userRequest.getEmail(), validatedUser.getEmail());
        verify(userRepository).findByEmail(userRequest.getEmail());
        verify(roleService).findByRoleName(anyString());
        verify(roleService).createRole(any(Role.class));
        verify(passwordEncoder).encode(anyString());
    }

    @Test
    public void testAuthenticate_Success() {
        AuthRequest authRequest = new AuthRequest();
        authRequest.setEmail("test@example.com");
        authRequest.setPassword("password");

        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);

        String accessToken = "accessToken";
        String refreshToken = "refreshToken";

        when(jwtUtils.generateToken(authRequest.getEmail())).thenReturn(accessToken);
        when(jwtUtils.generateRefreshToken(authRequest.getEmail())).thenReturn(refreshToken);

        JwtResponse jwtResponse = userService.authenticate(authRequest);

        assertNotNull(jwtResponse);
        assertEquals(accessToken, jwtResponse.getAccessToken());
        assertEquals(refreshToken, jwtResponse.getRefreshToken());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtils).generateToken(authRequest.getEmail());
        verify(jwtUtils).generateRefreshToken(authRequest.getEmail());
        verify(tokenService).saveToken(accessToken, refreshToken, authRequest.getEmail());
    }

    @Test
    public void testAuthenticate_Failure() {
        AuthRequest authRequest = new AuthRequest();
        authRequest.setEmail("test@example.com");
        authRequest.setPassword("password");

        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(false);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);

        assertThrows(BadCredentialsException.class, () -> userService.authenticate(authRequest));

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    public void testGetAllUsers() {
        User user1 = new User();
        user1.setId(1L);
        user1.setEmail("user1@example.com");

        User user2 = new User();
        user2.setId(2L);
        user2.setEmail("user2@example.com");

        List<User> userList = Arrays.asList(user1, user2);
        when(userRepository.findAll()).thenReturn(userList);

        List<UserResponse> userResponses = userService.getAllUsers();

        assertNotNull(userResponses);
        assertEquals(userList.size(), userResponses.size());
        verify(userRepository, times(1)).findAll();

        userResponses.forEach(response -> {
            User user = userList.stream()
                    .filter(u -> u.getId().equals(response.getId()))
                    .findFirst()
                    .orElse(null);
            assertNotNull(user);
            assertEquals(user.getEmail(), response.getEmail());
        });
    }

    @Test
    public void testGetCurrentUserProfile_UserFound() {
        String userName = "test@example.com";
        User user = new User();
        user.setId(1L);
        user.setEmail(userName);

        when(userRepository.findByEmail(userName)).thenReturn(Optional.of(user));

        UserResponse userResponse = userService.getCurrentUserProfile(userName);

        assertNotNull(userResponse);
        assertEquals(user.getId(), userResponse.getId());
        assertEquals(user.getEmail(), userResponse.getEmail());
        verify(userRepository).findByEmail(userName);
    }

    @Test
    public void testGetCurrentUserProfile_UserNotFound() {
        String userName = "test@example.com";
        when(userRepository.findByEmail(userName)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getCurrentUserProfile(userName));

        verify(userRepository).findByEmail(userName);
    }

    @Test
    public void testLogoutUser_Success() {
        String token = "validToken";
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(tokenService.deleteToken(token)).thenReturn(true);

        String result = userService.logoutUser(request, response);

        assertEquals("Logout successfully", result);
        verify(tokenService).deleteToken(token);
    }

    @Test
    public void testLogoutUser_Failure() {
        String token = "validToken";
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(tokenService.deleteToken(token)).thenReturn(false);

        String result = userService.logoutUser(request, response);

        assertNull(result);
        verify(tokenService).deleteToken(token);
    }

    @Test
    public void testGetUserById_UserExists() {
        User user = new User();
        user.setId(1L);
        user.setName("Test User");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Optional<User> result = userService.getUserById("1");
        assertEquals(Optional.of(user), result);
    }

    @Test
    public void testGetUserById_UserDoesNotExist() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        Optional<User> result = userService.getUserById("2");

        assertEquals(Optional.empty(), result);
    }

    @Test
    public void testMapEntityToResponse() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");

        UserResponse userResponse = userService.mapEntityToResponse(user);

        assertNotNull(userResponse);
        assertEquals(user.getId(), userResponse.getId());
        assertEquals(user.getEmail(), userResponse.getEmail());
    }
}