package com.flora.spring_boot.dto.request;

import com.flora.spring_boot.validator.DobConstraint;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreationRequest {
    @Size(min = 4, message = "USERNAME_INVALID" )
    String username;
    @Size(min = 6, message = "PASSWORD_INVALID")
    String password;
    String lastName;
    String firstName;
    @DobConstraint(min = 10, message = "DOB_INVALID")
    LocalDate dob;




}
