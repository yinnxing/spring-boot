package com.flora.spring_boot.mapper;

import com.flora.spring_boot.dto.request.PermissionRequest;
import com.flora.spring_boot.dto.response.PermissionResponse;
import com.flora.spring_boot.entity.Permission;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")

public interface PermissionMapper {
    Permission toPermission(PermissionRequest request);
    PermissionResponse toPermissionResponse(Permission permission);

}
