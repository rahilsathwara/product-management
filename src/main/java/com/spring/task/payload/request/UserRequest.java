package com.spring.task.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserRequest {

    @NotEmpty(message = "Name is required")
    @Size(min = 1, max = 30, message = "Name must be between 1 and 30 characters")
    private String name;

    @NotEmpty(message = "Email is required")
    @Size(min = 8, max = 100, message = "Email must be between 8 and 100 characters")
    @Email(message = "Invalid email format")
    private String email;

    @NotEmpty(message = "Password is required")
    @Size(min = 6, max = 15, message = "Password must be between 6 and 15 characters")
    private String password;

    @NotEmpty(message = "Confirm password is required")
    private String confirmPassword;

    @NotEmpty(message = "At least one role is required")
    private Set<String> roles;
}