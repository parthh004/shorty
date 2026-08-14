package com.tss.shorty.service;

import com.tss.shorty.payload.request.UpdateUserProfileRequestDto;
import com.tss.shorty.payload.response.UserProfileResponseDto;
import org.springframework.web.multipart.MultipartFile;

public interface IUserService
{
    UserProfileResponseDto getMyProfile();
    UserProfileResponseDto updateMyProfile(UpdateUserProfileRequestDto request);
    UserProfileResponseDto uploadProfilePicture(MultipartFile file);
    void deleteProfilePicture();
}
