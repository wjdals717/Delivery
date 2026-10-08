package com.example.delivery.user.dto.response;

import com.example.delivery.user.entity.UserRoleEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {
    private String username;
    private UserRoleEnum role;
    private String accessToken;
}
