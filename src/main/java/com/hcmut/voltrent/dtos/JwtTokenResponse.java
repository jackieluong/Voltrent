package com.hcmut.voltrent.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtTokenResponse {
    private String token;
    private long expiresAt;
}
