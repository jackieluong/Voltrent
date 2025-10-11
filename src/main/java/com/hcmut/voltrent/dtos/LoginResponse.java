package com.hcmut.voltrent.dtos;

import com.hcmut.voltrent.constant.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class LoginResponse {

    private String accessToken;
    private String refreshToken;
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
