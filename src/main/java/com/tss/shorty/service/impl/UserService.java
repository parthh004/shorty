package com.tss.shorty.service.impl;

import com.tss.shorty.entity.User;
import com.tss.shorty.exception.ResourceAlreadyExistsException;
import com.tss.shorty.mapper.UserMapper;
import com.tss.shorty.payload.request.UpdateUserProfileRequestDto;
import com.tss.shorty.payload.response.UserProfileResponseDto;
import com.tss.shorty.repository.UserRepository;
import com.tss.shorty.service.CloudinaryService;
import com.tss.shorty.service.CurrentUserProvider;
import com.tss.shorty.service.IUserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService;
    private final UserMapper userMapper;
    private final CurrentUserProvider currentUserProvider;

    @Override
    public UserProfileResponseDto getMyProfile() {
        User user = currentUserProvider.get();

        return userMapper.toUserProfileResponseDto(user);
    }

    @Override
    @Transactional
    public UserProfileResponseDto updateMyProfile(UpdateUserProfileRequestDto request) {
        User user = currentUserProvider.get();

        if (!user.getPhoneNo().equals(request.getPhoneNo()) && userRepository.existsByPhoneNo(request.getPhoneNo())) {
            throw new ResourceAlreadyExistsException("Phone number is already in use by another account.");
        }

        user.setUserName(request.getUserName());
        user.setPhoneNo(request.getPhoneNo());

        User updatedUser = userRepository.save(user);
        return userMapper.toUserProfileResponseDto(updatedUser);
    }

    @Override
    @Transactional
    public UserProfileResponseDto uploadProfilePicture(MultipartFile file) {
        User user = currentUserProvider.get();
        String oldImageUrl = user.getProfilePicture();
        try {
            String imageUrl = cloudinaryService.uploadProfilePicture(file);
            user.setProfilePicture(imageUrl);
            User savedUser = userRepository.save(user);

            if (oldImageUrl != null) {
                cloudinaryService.deleteImageFromCloudinary(oldImageUrl);
            }
            return userMapper.toUserProfileResponseDto(savedUser);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload profile picture: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void deleteProfilePicture() {
        User user = currentUserProvider.get();
        String oldImageUrl = user.getProfilePicture();

        if (oldImageUrl != null) {
            user.setProfilePicture(null);
            userRepository.save(user);
            cloudinaryService.deleteImageFromCloudinary(oldImageUrl);
        }
    }

    @Override
    public void addUrlSlots(User user, int quantity) {
        int currentSlots = user.getAvailableSlots();

        user.setAvailableSlots(currentSlots + quantity);
        userRepository.save(user);
    }
}
