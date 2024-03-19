package com.spring.task.controller;

import com.spring.task.entity.Role;
import com.spring.task.exception.ResourceNotFoundException;
import com.spring.task.payload.request.RoleRequest;
import com.spring.task.payload.response.RoleResponse;
import com.spring.task.service.RoleService;
import com.spring.task.web.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controller class for handling role-related endpoints.
 * Provides APIs for managing roles such as creating, updating, deleting, and retrieving roles.
 *
 * @author Rahil Sathwara
 * @since 2024-03-20
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/roles/")
public class RoleController {

    private final RoleService roleService;

    /**
     * Creates a new role with the provided details.
     * Only users with ROLE_ADMIN authority can access this endpoint.
     * Creates a new role based on the information provided in the request body.
     *
     * @param roleRequest The request body containing the details of the new role.
     * @return A response entity with a success message and the created role details.
     */
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse> createRole(@Valid @RequestBody RoleRequest roleRequest) {
        Role newRole = roleService.createNewRole(roleRequest);

        return new ResponseEntity<>(new ApiResponse(LocalDateTime.now(), HttpStatus.CREATED, "Role created successfully", roleService.mapEntityToResponse(newRole)), HttpStatus.CREATED);
    }

    /**
     * Retrieves a role by its ID.
     * Users with ROLE_ADMIN or ROLE_MANAGER authority can access this endpoint.
     * Retrieves a role based on the provided role ID.
     *
     * @param id The ID of the role to retrieve.
     * @return A response entity with the retrieved role details.
     * @throws ResourceNotFoundException If the role with the given ID is not found.
     */
    @GetMapping("{roleId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<ApiResponse> getRoleById(@PathVariable("roleId") Long id) {
        Role getRole = roleService.getRoleById(id).orElseThrow(() -> new ResourceNotFoundException("Error: Role is not found."));

        return new ResponseEntity<>(new ApiResponse(LocalDateTime.now(), HttpStatus.OK, "Get all roles", roleService.mapEntityToResponse(getRole)), HttpStatus.OK);
    }

    /**
     * Retrieves all roles.
     * Users with ROLE_ADMIN or ROLE_MANAGER authority can access this endpoint.
     * Retrieves a list of all roles available in the system.
     *
     * @return A response entity with the list of roles.
     */
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<ApiResponse> getAllRoles() {
        List<RoleResponse> getRoles = roleService.getAllRoles();

        return new ResponseEntity<>(new ApiResponse(LocalDateTime.now(), HttpStatus.OK, "Get role", getRoles), HttpStatus.OK);
    }

    /**
     * Updates an existing role.
     * Only users with ROLE_ADMIN authority can access this endpoint.
     * Updates the role details with the provided ID.
     *
     * @param id          The ID of the role to be updated.
     * @param roleRequest The request body containing the updated role details.
     * @return A response entity with the updated role information.
     * @throws ResourceNotFoundException   If the role with the given ID is not found.
     */
    @PutMapping("{roleId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse> editRole(@PathVariable("roleId") Long id, @Valid @RequestBody RoleRequest roleRequest) {
        Role savedRole = roleService.updateRole(id, roleRequest);

        return new ResponseEntity<>(new ApiResponse(LocalDateTime.now(), HttpStatus.OK, "Role updated successfully", roleService.mapEntityToResponse(savedRole)), HttpStatus.OK);
    }

    /**
     * Deletes a role.
     * Only users with ROLE_ADMIN authority can access this endpoint.
     * Deletes the role with the provided ID.
     *
     * @param id The ID of the role to be deleted.
     * @return A response entity indicating the success of the deletion.
     * @throws ResourceNotFoundException If the role with the given ID is not found.
     */
    @DeleteMapping("{roleId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse> deleteRole(@PathVariable("roleId") Long id) {
        roleService.deleteById(id);
        return new ResponseEntity<>(new ApiResponse(LocalDateTime.now(), HttpStatus.OK, "Delete role: " + id, null), HttpStatus.OK);
    }
}