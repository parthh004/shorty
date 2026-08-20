package com.tss.shorty.controller;

import com.tss.shorty.payload.request.UpdateUserProfileRequestDto;
import com.tss.shorty.payload.response.UserProfilePictureResponseDto;
import com.tss.shorty.payload.response.UserProfileResponseDto;
import com.tss.shorty.payload.response.UserStatsResponseDto;
import com.tss.shorty.payload.response.UserUpdateResponseDto;
import com.tss.shorty.service.IUserService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/users")
@PreAuthorize("hasRole('USER')")
public class UserController
{
    private final IUserService userService;


    public UserController(IUserService userService)
    {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponseDto> getMyProfile()
    {
        return ResponseEntity.ok(userService.getMyProfile());
    }

    @PutMapping("/me")
    public ResponseEntity<UserUpdateResponseDto> updateMyProfile(@Valid @RequestBody UpdateUserProfileRequestDto request)
    {
        return ResponseEntity.ok(userService.updateMyProfile(request));
    }

    @PutMapping(value = "/me/profile-picture", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserProfilePictureResponseDto> uploadProfilePicture(@RequestPart("file") MultipartFile file)
    {
        return ResponseEntity.ok(userService.uploadProfilePicture(file));
    }

    @DeleteMapping("/me/profile-picture")
    public ResponseEntity<Void> deleteProfilePicture()
    {
        userService.deleteProfilePicture();
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stats")
    public ResponseEntity<UserStatsResponseDto> getMyStats() {
        return ResponseEntity.ok(userService.getUserStats());
    }
}
