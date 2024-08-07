package com.flora.spring_boot.mapper;

import com.flora.spring_boot.dto.request.UserCreationRequest;
import com.flora.spring_boot.dto.request.UserUpdateRequest;
import com.flora.spring_boot.dto.response.UserResponse;
import com.flora.spring_boot.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserCreationRequest request);
    UserResponse toUserResponse(User user);
    @Mapping(target = "roles", ignore = true)

    void updateUser(@MappingTarget User user, UserUpdateRequest request);


}
