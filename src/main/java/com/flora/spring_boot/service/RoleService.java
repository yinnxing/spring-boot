package com.flora.spring_boot.service;

import com.flora.spring_boot.dto.request.RoleRequest;
import com.flora.spring_boot.dto.response.RoleResponse;
import com.flora.spring_boot.entity.Permission;
import com.flora.spring_boot.entity.Role;
import com.flora.spring_boot.mapper.RoleMapper;
import com.flora.spring_boot.repository.PermissionRepository;
import com.flora.spring_boot.repository.RoleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleService {
    RoleRepository roleRepository;
    RoleMapper roleMapper;
    PermissionRepository permissionRepository;

    public RoleResponse create(RoleRequest request){
        Role role = roleMapper.toRole(request);
        List<Permission> permissions = permissionRepository.findAllById(request.getPermissions());
        role.setPermissions(new HashSet<>(permissions));
        roleRepository.save(role);
        return roleMapper.toRoleResponse(role);
    }

    public List<RoleResponse> getAll(){
        List<Role> roles = roleRepository.findAll();
        return roles.stream()
                .map(roleMapper::toRoleResponse)
                .toList();
    }
    public void delete(String role){
        roleRepository.deleteById(role);
    }

}
