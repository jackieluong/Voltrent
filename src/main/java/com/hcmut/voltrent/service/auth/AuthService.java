package com.hcmut.voltrent.service.auth;

import com.hcmut.voltrent.constant.Role;
import com.hcmut.voltrent.constant.TokenType;
import com.hcmut.voltrent.dtos.model.JwtTokenResponse;
import com.hcmut.voltrent.dtos.request.LoginRequest;
import com.hcmut.voltrent.dtos.response.LoginResponse;
import com.hcmut.voltrent.dtos.request.RegisterRequest;
import com.hcmut.voltrent.dtos.model.UserDto;
import com.hcmut.voltrent.entity.User;
import com.hcmut.voltrent.repository.UserRepository;
import com.hcmut.voltrent.security.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    @Autowired
    public AuthService(UserRepository userRepository, JwtUtil jwtUtil, AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
    }

    public LoginResponse login(LoginRequest loginRequest) {

        User user = userRepository.findByEmail(loginRequest.getEmail())
                .map(u -> {
                    if (!passwordEncoder.matches(loginRequest.getPassword(), u.getPassword())) {
                        log.error("Invalid password for user with email: {}", loginRequest.getEmail());
                        throw new BadCredentialsException("Invalid email or password");
                    }
                    return u;
                })
                .orElseThrow(() -> {
                    log.error("No user found with email: {}", loginRequest.getEmail());
                    return new BadCredentialsException("Invalid email or password");
                });

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail(), loginRequest.getPassword());
        Authentication authentication = authenticationManager.authenticate(token);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        JwtTokenResponse accessToken = jwtUtil.generateAccessToken(String.valueOf(user.getId()),
                Map.of("role", user.getRole(), "email", user.getEmail())
        );


        JwtTokenResponse refreshToken = jwtUtil.generateRefreshToken(String.valueOf(user.getId()),
                Map.of("role", user.getRole()));

        log.info("Login successfully for user with email: {}", loginRequest.getEmail());
        return LoginResponse.builder()
                .accessToken(accessToken.getToken())
                .accessTokenExpiresIn(accessToken.getExpiresAt())
                .refreshToken(refreshToken.getToken())
                .refreshTokenExpiresIn(refreshToken.getExpiresAt())
                .tokenType(TokenType.BEARER.getValue())
                .user(new LoginResponse.UserDto(user.getId(), user.getEmail(), user.getFullname(), user.getPhone(), user.getRole()))
                .build();
    }

    public UserDto register(RegisterRequest registerRequest) {
        userRepository.findByEmail(registerRequest.getEmail())
                .ifPresent(user -> {
                    log.error("Email already exists: {}", registerRequest.getEmail());
                    throw new IllegalArgumentException("Email already exists");
                });

        User user = User.builder()
                .email(registerRequest.getEmail())
                .phone(registerRequest.getPhone())
                .fullname(registerRequest.getFullname())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(Role.fromValue(registerRequest.getRole()).getValue())
                .build();

        try {
            userRepository.save(user);
            log.info("Register successfully for user with email: {}", registerRequest.getEmail());
            return modelMapper.map(user, UserDto.class);
        } catch (Exception e) {
            log.error("Error registering user {}", registerRequest.getEmail(), e);
            throw new RuntimeException("Error registering user");
        }

    }

}
