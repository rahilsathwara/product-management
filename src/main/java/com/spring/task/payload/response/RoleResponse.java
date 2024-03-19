package com.spring.task.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RoleResponse {

    private Long id;
    private String name;
    private LocalDateTime createdAt;
}
