package com.flora.spring_boot.service;

import com.flora.spring_boot.dto.request.PermissionRequest;
import com.flora.spring_boot.dto.response.PermissionResponse;
import com.flora.spring_boot.entity.Permission;
import com.flora.spring_boot.mapper.PermissionMapper;
import com.flora.spring_boot.repository.PermissionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionService {
    PermissionRepository permissionRepository;
    PermissionMapper permissionMapper;

    public PermissionResponse create(PermissionRequest request){
        Permission permission = permissionMapper.toPermission(request);
        permissionRepository.save(permission);
        return permissionMapper.toPermissionResponse(permission);
    }

    public List<PermissionResponse> getAll(){
        List<Permission> permissions = permissionRepository.findAll();
        return permissions.stream().map(permissionMapper::toPermissionResponse).toList();
    }


    public void delete(String permission){
        permissionRepository.deleteById(permission);
    }
}
