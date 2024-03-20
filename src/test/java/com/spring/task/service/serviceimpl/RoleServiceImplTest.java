package com.spring.task.service.serviceimpl;

import com.spring.task.entity.Role;
import com.spring.task.exception.ResourceAlreadyExistException;
import com.spring.task.exception.ResourceNotFoundException;
import com.spring.task.payload.request.RoleRequest;
import com.spring.task.payload.response.RoleResponse;
import com.spring.task.repository.RoleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RoleServiceImplTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleServiceImpl roleService;

    @Test
    public void testFindByRoleName_Success() {
        RoleRequest roleRequest = new RoleRequest();
        roleRequest.setName("ROLE_TEST");

        when(roleRepository.findByNameIgnoreCase(roleRequest.getName())).thenReturn(Optional.empty());

        Role role = new Role();
        role.setName(roleRequest.getName());
        role.setCreatedAt(LocalDateTime.now());

        when(roleRepository.save(any(Role.class))).thenReturn(role);

        Role createdRole = roleService.createNewRole(roleRequest);

        assertNotNull(createdRole);
        assertEquals(role.getName(), createdRole.getName());
        verify(roleRepository).findByNameIgnoreCase(roleRequest.getName());
        verify(roleRepository).save(any(Role.class));
    }

    @Test
    public void testCreateNewRole_AlreadyExists() {
        RoleRequest roleRequest = new RoleRequest();
        roleRequest.setName("ROLE_TEST");

        Role existingRole = new Role();
        existingRole.setName(roleRequest.getName());

        when(roleRepository.findByNameIgnoreCase(roleRequest.getName())).thenReturn(Optional.of(existingRole));

        assertThrows(ResourceAlreadyExistException.class, () -> roleService.createNewRole(roleRequest));

        verify(roleRepository).findByNameIgnoreCase(roleRequest.getName());
        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    public void testFindByRoleName_NotFound() {
        String roleName = "Test Role";
        when(roleRepository.findByNameIgnoreCase(roleName)).thenReturn(Optional.empty());

        Optional<Role> result = roleService.findByRoleName(roleName);
        assertFalse(result.isPresent());
        verify(roleRepository).findByNameIgnoreCase(roleName);
    }

    @Test
    public void testCreateRole_Success() {
        Role newRole = new Role();
        newRole.setName("New Role");

        when(roleRepository.save(newRole)).thenReturn(newRole);

        Role createdRole = roleService.createRole(newRole);

        assertNotNull(createdRole);
        assertEquals(newRole, createdRole);
        verify(roleRepository).save(newRole);
    }

    @Test
    public void testGetRoleById_Success() {
        Long roleId = 1L;
        Role role = new Role();
        role.setId(roleId);

        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));

        Optional<Role> result = roleService.getRoleById(roleId);

        assertTrue(result.isPresent());
        assertEquals(role, result.get());
        verify(roleRepository).findById(roleId);
    }

    @Test
    public void testGetRoleById_NotFound() {
        Long roleId = 1L;

        when(roleRepository.findById(roleId)).thenReturn(Optional.empty());

        Optional<Role> result = roleService.getRoleById(roleId);

        assertFalse(result.isPresent());
        verify(roleRepository).findById(roleId);
    }

    @Test
    public void testGetAllRoles_Success() {
        Role role1 = new Role();
        role1.setId(1L);
        role1.setName("Role 1");
        role1.setCreatedAt(LocalDateTime.now());

        Role role2 = new Role();
        role2.setId(2L);
        role2.setName("Role 2");
        role2.setCreatedAt(LocalDateTime.now());

        List<Role> roles = List.of(role1, role2);

        when(roleRepository.findAll()).thenReturn(roles);

        List<RoleResponse> roleResponses = roleService.getAllRoles();

        assertNotNull(roleResponses);
        assertEquals(2, roleResponses.size());

        verify(roleRepository).findAll();
    }

    @Test
    public void testGetAllRoles_NoRoles() {
        when(roleRepository.findAll()).thenReturn(Collections.emptyList());

        List<RoleResponse> roleResponses = roleService.getAllRoles();

        assertNotNull(roleResponses);
        assertTrue(roleResponses.isEmpty());
        verify(roleRepository).findAll();
    }

    @Test
    public void testDeleteById_Success() {
        Long roleId = 1L;
        Role role = new Role();
        role.setId(roleId);

        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));
        roleService.deleteById(roleId);

        verify(roleRepository).delete(role);
    }

    @Test
    public void testDeleteById_NotFound() {
        Long roleId = 1L;

        when(roleRepository.findById(roleId)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> roleService.deleteById(roleId));
    }

    @Test
    public void testUpdateRole_Success() {
        // Given
        Long roleId = 1L;
        RoleRequest roleRequest = new RoleRequest();
        roleRequest.setName("Updated Role");

        Role existingRole = new Role();
        existingRole.setId(roleId);
        existingRole.setName("Existing Role");

        when(roleRepository.findById(roleId)).thenReturn(Optional.of(existingRole));
        when(roleRepository.existsByNameIgnoreCaseAndIdNot(roleRequest.getName(), roleId)).thenReturn(false);
        when(roleRepository.save(existingRole)).thenReturn(existingRole);

        // When
        Role updatedRole = roleService.updateRole(roleId, roleRequest);

        // Then
        assertNotNull(updatedRole);
        assertEquals(roleRequest.getName(), updatedRole.getName());
        assertNotNull(updatedRole.getUpdatedAt());
        verify(roleRepository).save(existingRole);
    }

    @Test
    public void testUpdateRole_NotFound() {
        Long roleId = 1L;
        RoleRequest roleRequest = new RoleRequest();
        roleRequest.setName("Updated Role");

        when(roleRepository.findById(roleId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> roleService.updateRole(roleId, roleRequest));
    }

    @Test
    public void testUpdateRole_AlreadyExists() {
        Long roleId = 1L;
        RoleRequest roleRequest = new RoleRequest();
        roleRequest.setName("Existing Role");

        Role existingRole = new Role();
        existingRole.setId(roleId);
        existingRole.setName("Current Role");

        when(roleRepository.findById(roleId)).thenReturn(Optional.of(existingRole));
        when(roleRepository.existsByNameIgnoreCaseAndIdNot(roleRequest.getName(), roleId)).thenReturn(true);

        assertThrows(ResourceAlreadyExistException.class, () -> roleService.updateRole(roleId, roleRequest));
    }

    @Test
    public void testMapEntityToResponse() {
        Long roleId = 1L;
        String roleName = "Test Role";
        LocalDateTime createdAt = LocalDateTime.now();

        Role role = new Role();
        role.setId(roleId);
        role.setName(roleName);
        role.setCreatedAt(createdAt);

        RoleResponse roleResponse = roleService.mapEntityToResponse(role);

        assertNotNull(roleResponse);
        assertEquals(roleId, roleResponse.getId());
        assertEquals(roleName, roleResponse.getName());
        assertEquals(createdAt, roleResponse.getCreatedAt());
    }
}