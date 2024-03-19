package com.spring.task.service;

import com.spring.task.entity.User;
import com.spring.task.payload.request.AuthRequest;
import com.spring.task.payload.request.UserRequest;
import com.spring.task.payload.response.JwtResponse;
import com.spring.task.payload.response.UserResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

public interface UserService {

    User createUser(UserRequest userRequest);

    UserResponse mapEntityToResponse(User user);

    JwtResponse authenticate(AuthRequest authRequest);

    List<UserResponse> getAllUsers();

    UserResponse getCurrentUserProfile(String userName);

    String logoutUser(HttpServletRequest request, HttpServletResponse response);
}
