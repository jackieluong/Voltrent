package com.hcmut.voltrent.dtos.response;

import com.hcmut.voltrent.constant.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class LoginResponse {

    private String accessToken;
    private long accessTokenExpiresIn;
    private String refreshToken;
    private long refreshTokenExpiresIn;
    private String tokenType;
    private UserDto user;

    @AllArgsConstructor
    @Getter
    @Setter
    public static class UserDto {
        private String email;
        private String fullname;
        private String phone;
        private Role role;
    }
}
