package com.spring.task.service;

import com.spring.task.entity.Role;
import com.spring.task.payload.request.RoleRequest;
import com.spring.task.payload.response.RoleResponse;

import java.util.List;
import java.util.Optional;

public interface RoleService {
    Optional<Role> findByRoleName(String role);
    Role createRole(Role newRole);
    Role createNewRole(RoleRequest roleRequest);

    RoleResponse mapEntityToResponse(Role role);

    Optional<Role> getRoleById(Long id);

    List<RoleResponse> getAllRoles();

    void deleteById(Long id);

    Role updateRole(Long id, RoleRequest roleRequest);
}