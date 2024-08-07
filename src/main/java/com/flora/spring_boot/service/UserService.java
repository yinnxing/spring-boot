package com.flora.spring_boot.service;

import com.flora.spring_boot.dto.request.UserCreationRequest;
import com.flora.spring_boot.dto.request.UserUpdateRequest;
import com.flora.spring_boot.dto.response.UserResponse;
import com.flora.spring_boot.entity.User;
import com.flora.spring_boot.enums.Role;
import com.flora.spring_boot.exception.AppException;
import com.flora.spring_boot.exception.ErrorCode;
import com.flora.spring_boot.mapper.UserMapper;
import com.flora.spring_boot.repository.RoleRepository;
import com.flora.spring_boot.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    RoleRepository roleRepository;
    public UserResponse createUser(UserCreationRequest request){

        if(userRepository.existsByUsername(request.getUsername())){
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        return userMapper.toUserResponse(userRepository.save(user));
    }
@PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getUsers() {
        List<User> users = userRepository.findAll();
        List<UserResponse> userResponses = new ArrayList<>();

        users.forEach(user -> userResponses.add(userMapper.toUserResponse(user)));

        return userResponses;
    }


@PostAuthorize("returnObject.username == authentication.name")
    public UserResponse getUser(String Id) {
        return userMapper.toUserResponse(userRepository.findById(Id).orElseThrow(() ->
               new RuntimeException("User not found")));
    }

    public UserResponse updateUser(String id, UserUpdateRequest request){
        User user = userRepository.findById(id).orElseThrow(() ->
                new AppException(ErrorCode.USER_NOT_EXIST));
        userMapper.updateUser(user, request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        var roles = roleRepository.findAllById(request.getRoles());
        user.setRoles(new HashSet<>(roles));
        return userMapper.toUserResponse(userRepository.save(user));

    }

    public void deleteUser(String userId) {
        userRepository.deleteById(userId);
    }

    public UserResponse getMyInfo() {
        var context = SecurityContextHolder.getContext();
        String username = context.getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXIST)
        );
        return userMapper.toUserResponse(user);

    }
}
