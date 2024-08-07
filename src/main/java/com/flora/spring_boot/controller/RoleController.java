package com.flora.spring_boot.controller;

import com.flora.spring_boot.dto.request.RoleRequest;
import com.flora.spring_boot.dto.response.ApiResponse;
import com.flora.spring_boot.service.RoleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleController {
    RoleService roleService;
    @PostMapping
    ApiResponse create(@RequestBody RoleRequest request){
        return ApiResponse.builder()
                .result(roleService.create(request))
                .build();

    }
    @GetMapping
    ApiResponse getAll(){
        return ApiResponse.builder()
                .result(roleService.getAll())
                .build();
    }

    @DeleteMapping("/{role}")
    ApiResponse delete(@PathVariable String role){
        roleService.delete(role);
        return ApiResponse.builder().build();
    }
}
