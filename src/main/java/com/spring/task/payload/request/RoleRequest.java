package com.spring.task.payload.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RoleRequest {

    @NotEmpty(message = "Role name cannot be empty/null")
    @Size(min = 1, max = 30, message = "Role name size should be minimum of length 1.")
    private String name;
}
