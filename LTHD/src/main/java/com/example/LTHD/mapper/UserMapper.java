package com.example.LTHD.mapper;

import com.example.LTHD.dto.request.UserCreateRequest;
import com.example.LTHD.dto.request.UserUpdateRequest;
import com.example.LTHD.dto.response.UserResponse;
import com.example.LTHD.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {

    // UserCreateRequest -> User
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    User toUser(UserCreateRequest request);

    // UserUpdateRequest -> User đang tồn tại
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    void updateUser(
            @MappingTarget User user,
            UserUpdateRequest request);

    // User -> UserResponse
    UserResponse toUserResponse(User user);
}