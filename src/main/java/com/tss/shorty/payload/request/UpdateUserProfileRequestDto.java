package com.tss.shorty.payload.request;

import com.tss.shorty.util.Validator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserProfileRequestDto
{
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String userName;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = Validator.PHONE_REGEX, message = "Invalid phone number (must be 10 digits)")
    private String phoneNo;
}
