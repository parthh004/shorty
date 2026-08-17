package com.tss.shorty.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponseDto
{
    private UUID userId;
    private String userName;
    private String email;
    private String phoneNo;
    private Boolean isActive;
    private Boolean isEmailVerified;
    private String profilePicture;
    private Integer availableSlots;
    private LocalDateTime createdOn;
    private LocalDateTime updatedOn;
}
