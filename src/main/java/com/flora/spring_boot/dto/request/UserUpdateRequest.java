package com.flora.spring_boot.dto.request;
import com.flora.spring_boot.entity.Role;
import com.flora.spring_boot.validator.DobConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class UserUpdateRequest {
    private String password;
    private String firstName;
    private String lastName;
    @DobConstraint(min = 18, message = "DOB_INVALID")
    private LocalDate dob;
    private List<String> roles;


}
