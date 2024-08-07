package com.flora.spring_boot.controller;

import com.flora.spring_boot.dto.request.PermissionRequest;
import com.flora.spring_boot.dto.response.ApiResponse;
import com.flora.spring_boot.dto.response.PermissionResponse;
import com.flora.spring_boot.service.PermissionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/permissions")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionController {
    PermissionService permissionService;
    @PostMapping
    ApiResponse create(@RequestBody PermissionRequest request){
        return ApiResponse.builder()
                .result(permissionService.create(request))
                .build();

    }
    @GetMapping
    ApiResponse getAll(){
        return ApiResponse.builder()
                .result(permissionService.getAll())
                .build();
    }

    @DeleteMapping("/{permission}")
    ApiResponse delete(@PathVariable String permission){
        permissionService.delete(permission);
        return ApiResponse.builder().build();
    }
}
