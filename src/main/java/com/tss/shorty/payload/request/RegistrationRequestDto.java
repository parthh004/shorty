package com.tss.shorty.payload.request;

import com.tss.shorty.entity.enums.Role;
import com.tss.shorty.util.Validator;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationRequestDto
{
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String userName;

    @NotBlank(message = "Email is required")
    @Email(message = "Must be a well-formed email address")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = Validator.PHONE_REGEX, message = "Invalid phone number (must be 10 digits)")
    private String phoneNo;

    @NotBlank(message = "Password is required")
    @Pattern(regexp = Validator.PASSWORD_REGEX, message = "Password must be at least 8 characters long, contain at least one digit, one lowercase, one uppercase, and one special character")
    private String password;
//
//    @NotNull(message = "Role is required")
//    private Role role;

    private String profilePicture;
}
