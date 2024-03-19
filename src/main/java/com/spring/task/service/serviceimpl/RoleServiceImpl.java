package com.spring.task.service.serviceimpl;

import com.spring.task.entity.Role;
import com.spring.task.exception.ResourceAlreadyExistException;
import com.spring.task.exception.ResourceNotFoundException;
import com.spring.task.payload.request.RoleRequest;
import com.spring.task.payload.response.RoleResponse;
import com.spring.task.repository.RoleRepository;
import com.spring.task.service.RoleService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private static final Logger logger = LoggerFactory.getLogger(RoleServiceImpl.class);

    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public Role createNewRole(RoleRequest roleRequest) {
        logger.info("Creating new role with name: {}", roleRequest.getName());

        // validate role
        roleRepository.findByNameIgnoreCase(roleRequest.getName())
                .ifPresent(role -> {
                    logger.error("Role already exists with name: {}", roleRequest.getName());
                    throw new ResourceAlreadyExistException(roleRequest.getName() + " is Role already exist");
                });
        Role role = new Role();
        role.setName(roleRequest.getName());
        role.setCreatedAt(LocalDateTime.now());

        return roleRepository.save(role);
    }

    @Override
    public Optional<Role> findByRoleName(String role) {
        return roleRepository.findByNameIgnoreCase(role);
    }

    @Override
    @Transactional
    public Role createRole(Role newRole) {
        return roleRepository.save(newRole);
    }

    @Override
    public Optional<Role> getRoleById(Long id) {
        return roleRepository.findById(id);
    }

    @Override
    public List<RoleResponse> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(this::mapEntityToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Role role = getRoleById(id)
                .orElseThrow(() -> {
                    logger.error("Role not found with id: {}", id);
                    return new ResourceNotFoundException("Error: Role is not found.");
                });

        roleRepository.delete(role);
    }

    @Override
    @Transactional
    public Role updateRole(Long id, RoleRequest roleRequest) {
        logger.info("Updating role with id: {}", id);

        Role role = getRoleById(id)
                .orElseThrow(() -> {
                    logger.error("Role not found with id: {}", id);
                    return new ResourceNotFoundException("Error: Role is not found.");
                });

        // check role name exist or not
        if (roleRepository.existsByNameIgnoreCaseAndIdNot(roleRequest.getName(), id)) {
            logger.error("Role with name {} already exists", roleRequest.getName());
            throw new ResourceAlreadyExistException("Role with name " + roleRequest.getName() + " already exists");
        }
        role.setName(roleRequest.getName());
        role.setUpdatedAt(LocalDateTime.now());

        return roleRepository.save(role);
    }

    @Override
    public RoleResponse mapEntityToResponse(Role role) {
        return new RoleResponse(role.getId(), role.getName(), role.getCreatedAt());
    }
}