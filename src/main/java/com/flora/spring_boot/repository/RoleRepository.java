package com.flora.spring_boot.repository;

import com.flora.spring_boot.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface RoleRepository extends JpaRepository<Role, String> {
}
