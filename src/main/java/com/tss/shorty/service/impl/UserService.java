package com.tss.shorty.service.impl;

import com.tss.shorty.entity.User;
import com.tss.shorty.exception.ResourceAlreadyExistsException;
import com.tss.shorty.mapper.UserMapper;
import com.tss.shorty.payload.request.UpdateUserProfileRequestDto;
import com.tss.shorty.payload.response.UserProfilePictureResponseDto;
import com.tss.shorty.payload.response.UserProfileResponseDto;
import com.tss.shorty.payload.response.UserStatsResponseDto;
import com.tss.shorty.payload.response.UserUpdateResponseDto;
import com.tss.shorty.repository.UrlRepository;
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
    private final UrlRepository urlRepository;
    private final CurrentUserProvider currentUserProvider;

    @Override
    public UserProfileResponseDto getMyProfile() {
        User user = currentUserProvider.get();

        return userMapper.toUserProfileResponseDto(user);
    }

    @Override
    @Transactional
    public UserUpdateResponseDto updateMyProfile(UpdateUserProfileRequestDto request) {
        User user = currentUserProvider.get();

        if (!user.getPhoneNo().equals(request.getPhoneNo()) && userRepository.existsByPhoneNo(request.getPhoneNo())) {
            throw new ResourceAlreadyExistsException("Phone number is already in use by another account.");
        }

        user.setUserName(request.getUserName());
        user.setPhoneNo(request.getPhoneNo());

        User updatedUser = userRepository.save(user);
        return userMapper.toUserUpdateResponseDto(updatedUser);
    }

    @Override
    @Transactional
    public UserProfilePictureResponseDto uploadProfilePicture(MultipartFile file) {
        User user = currentUserProvider.get();
        String oldImageUrl = user.getProfilePicture();
        try {
            String imageUrl = cloudinaryService.uploadProfilePicture(file);
            user.setProfilePicture(imageUrl);
            User savedUser = userRepository.save(user);

            if (oldImageUrl != null) {
                cloudinaryService.deleteImageFromCloudinary(oldImageUrl);
            }
            return userMapper.toUserProfilePictureDto(savedUser);
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

    @Override
    public UserStatsResponseDto getUserStats() {
        User currentUser = currentUserProvider.get();

        return UserStatsResponseDto.builder()
                .availableSlots(currentUser.getAvailableSlots())
                .totalUrlsCreated(urlRepository.countByUser(currentUser))
                .activeUrls(urlRepository.countByUserAndIsExpiredFalse(currentUser))
                .totalClicksReceived(urlRepository.sumClicksByUser(currentUser))
                .build();
    }
}
