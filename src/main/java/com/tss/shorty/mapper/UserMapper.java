package com.tss.shorty.mapper;

import com.tss.shorty.entity.User;
import com.tss.shorty.payload.request.RegistrationRequestDto;
import com.tss.shorty.payload.response.UserProfileResponseDto;
import com.tss.shorty.payload.response.UserSummaryDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper
{
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "isEmailVerified", ignore = true)
    @Mapping(target = "availableSlots", ignore = true)
    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "updatedOn", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "profilePicture", ignore = true)
    User toUserEntity(RegistrationRequestDto registrationDTO);


    UserProfileResponseDto toUserProfileResponseDto(User user);

    UserSummaryDto toUserSummaryDto(User user);

}