package com.spring.task.controller;

import com.spring.task.entity.Role;
import com.spring.task.exception.ResourceNotFoundException;
import com.spring.task.payload.request.RoleRequest;
import com.spring.task.payload.response.RoleResponse;
import com.spring.task.service.RoleService;
import com.spring.task.web.ApiResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RoleControllerTest {

    @Mock
    private RoleService roleService;

    @InjectMocks
    private RoleController roleController;


    @Test
    @WithMockUser(roles = "ADMIN")
    public void testCreateRole() {
        RoleRequest roleRequest = new RoleRequest();
        Role newRole = new Role();

        when(roleService.createNewRole(roleRequest)).thenReturn(newRole);
        when(roleService.mapEntityToResponse(newRole)).thenReturn(new RoleResponse());

        ResponseEntity<ApiResponse> response = roleController.createRole(roleRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Role created successfully", response.getBody().getMessage());
        assertEquals(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), response.getBody().getTimestamp().truncatedTo(ChronoUnit.SECONDS));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetRoleById_RoleFound() {
        Long roleId = 1L;
        Role role = new Role();

        when(roleService.getRoleById(roleId)).thenReturn(Optional.of(role));
        when(roleService.mapEntityToResponse(role)).thenReturn(new RoleResponse());

        ResponseEntity<ApiResponse> response = roleController.getRoleById(roleId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Get all roles", response.getBody().getMessage());
        assertEquals(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), response.getBody().getTimestamp().truncatedTo(ChronoUnit.SECONDS));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetRoleById_RoleNotFound() {
        Long roleId = 1L;
        when(roleService.getRoleById(roleId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> roleController.getRoleById(roleId));
        assertEquals("Error: Role is not found.", exception.getMessage());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetAllRoles() {
        List<RoleResponse> roleResponseList = new ArrayList<>();
        when(roleService.getAllRoles()).thenReturn(roleResponseList);

        ResponseEntity<ApiResponse> response = roleController.getAllRoles();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Get role", response.getBody().getMessage());
        assertEquals(roleResponseList, response.getBody().getData());
        assertEquals(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), response.getBody().getTimestamp().truncatedTo(ChronoUnit.SECONDS));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testEditRole() {
        Long roleId = 1L;
        RoleRequest roleRequest = new RoleRequest();

        Role updatedRole = new Role();

        when(roleService.updateRole(roleId, roleRequest)).thenReturn(updatedRole);
        when(roleService.mapEntityToResponse(updatedRole)).thenReturn(new RoleResponse());

        ResponseEntity<ApiResponse> response = roleController.editRole(roleId, roleRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Role updated successfully", response.getBody().getMessage());
        assertEquals(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), response.getBody().getTimestamp().truncatedTo(ChronoUnit.SECONDS));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testDeleteRole() {
        Long roleId = 1L;

        doNothing().when(roleService).deleteById(roleId);

        ResponseEntity<ApiResponse> response = roleController.deleteRole(roleId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Delete role: " + roleId, response.getBody().getMessage());
        assertEquals(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), response.getBody().getTimestamp().truncatedTo(ChronoUnit.SECONDS));
    }
}