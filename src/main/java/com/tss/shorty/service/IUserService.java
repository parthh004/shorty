package com.tss.shorty.service;

import com.tss.shorty.entity.User;
import com.tss.shorty.payload.request.UpdateUserProfileRequestDto;
import com.tss.shorty.payload.response.UserProfileResponseDto;
import jakarta.validation.constraints.Min;
import org.springframework.web.multipart.MultipartFile;

public interface IUserService {
    UserProfileResponseDto getMyProfile();

    UserProfileResponseDto updateMyProfile(UpdateUserProfileRequestDto request);

    UserProfileResponseDto uploadProfilePicture(MultipartFile file);

    void deleteProfilePicture();

    void addUrlSlots(User user, @Min(value = 1, message = "Quantity must be at least 1") int quantity);
}
