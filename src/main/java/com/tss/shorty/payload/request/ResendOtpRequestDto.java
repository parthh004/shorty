package com.tss.shorty.payload.request;

import com.tss.shorty.util.Validator;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResendOtpRequestDto
{
    @NotBlank(message = "Email required")
    @Email(message = "Invalid email format")
    private String email;
}