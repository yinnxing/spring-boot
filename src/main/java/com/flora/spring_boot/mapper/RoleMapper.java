package com.flora.spring_boot.mapper;

import com.flora.spring_boot.dto.request.RoleRequest;
import com.flora.spring_boot.dto.response.RoleResponse;
import com.flora.spring_boot.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")

public interface RoleMapper {
    @Mapping(target = "permissions", ignore = true)
    Role toRole(RoleRequest request);
    RoleResponse toRoleResponse(Role role);
}
