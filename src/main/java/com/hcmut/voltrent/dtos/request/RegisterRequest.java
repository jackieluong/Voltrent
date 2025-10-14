package com.hcmut.voltrent.dtos.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {

    private String fullname;
    private String email;
    private String password;
    private String phone;

    private String role;
}
