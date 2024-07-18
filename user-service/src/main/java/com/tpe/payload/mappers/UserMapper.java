package com.tpe.payload.mappers;

import com.tpe.domain.User;
import com.tpe.payload.request.UserRequest;
import com.tpe.payload.request.UserRequestForCreateOrUpdate;
import com.tpe.payload.request.UserRequestForRegister;
import com.tpe.payload.request.UserRequestForUpdatePassword;
import com.tpe.payload.response.UserResponse;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User mapUserRequestToUser(UserRequestForRegister userRequestForRegister) {
        return User.builder().email(userRequestForRegister.getEmail())
                .firstName(userRequestForRegister.getFirstName())
                .lastName(userRequestForRegister.getLastName())
                .address(userRequestForRegister.getAddress())
                .phone(userRequestForRegister.getPhone())
                .birthDate(userRequestForRegister.getBirthDate())
                .build();
    }

    public User mapUserRequestToUser(UserRequestForCreateOrUpdate userRequestForCreateOrUpdate) {
        return User.builder().email(userRequestForCreateOrUpdate.getEmail())
                .firstName(userRequestForCreateOrUpdate.getFirstName())
                .lastName(userRequestForCreateOrUpdate.getLastName())
                .address(userRequestForCreateOrUpdate.getAddress())
                .phone(userRequestForCreateOrUpdate.getPhone())
                .birthDate(userRequestForCreateOrUpdate.getBirthDate())
                .builtIn(userRequestForCreateOrUpdate.getBuiltIn())
                .build();
    }

    public User mapUserRequestForAdminToUser(UserRequest userRequest) {
        return User.builder().email(userRequest.getEmail())
                .password(userRequest.getPassword())
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .address(userRequest.getAddress())
                .phone(userRequest.getPhone())
                .birthDate(userRequest.getBirthDate())
                .build();
    }

    public UserResponse mapUserToUserResponse(User user) {
        return UserResponse.builder().email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .address(user.getAddress())
                .phone(user.getPhone())
                .birthDate(user.getBirthDate())
                .build();
    }

    public User mapUserRequestToUpdatedUser(UserRequestForCreateOrUpdate userRequestForCreateOrUpdate, Long userId) {
        return User.builder()
                .id(userId)
                .email(userRequestForCreateOrUpdate.getEmail())
                .firstName(userRequestForCreateOrUpdate.getFirstName())
                .lastName(userRequestForCreateOrUpdate.getLastName())
                .address(userRequestForCreateOrUpdate.getAddress())
                .phone(userRequestForCreateOrUpdate.getPhone())
                .birthDate(userRequestForCreateOrUpdate.getBirthDate())
                .builtIn(userRequestForCreateOrUpdate.getBuiltIn())
                .build();

    }

    public User mapUserRequestToUserUpdatedPassword(UserRequestForUpdatePassword userRequestForUpdatePassword, Long userId) {
        return User.builder()
                .id(userId)
                .password(userRequestForUpdatePassword.getPassword())
                .build();
    }
}
