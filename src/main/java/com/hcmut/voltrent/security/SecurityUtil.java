package com.hcmut.voltrent.security;

import io.jsonwebtoken.Jwt;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;
import java.util.Optional;

public class SecurityUtil {
    /**
     * Get the login of the current user.
     *
     * @return the login of the current user.
     */
    public static Optional<String> getCurrentUserLogin() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(extractPrincipal(securityContext.getAuthentication()));
    }

    private static String extractPrincipal(Authentication authentication) {

        if (authentication == null) {
            return null;
        } else if (authentication.getPrincipal() instanceof UserDetails springSecurityUser) {
            return springSecurityUser.getUsername();
        } else if (authentication.getPrincipal() instanceof String s) {
            return s;
        } else if(authentication.getPrincipal() instanceof Map){

            Map<String, String> principal = (Map<String, String>) authentication.getPrincipal();
            return principal.get("userId");
        }
        return null;
    }

    /**
     * Get the JWT of the current user.
     *
     * @return the JWT of the current user.
     */
    public static Optional<String> getCurrentUserJWT() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(securityContext.getAuthentication())
                .filter(authentication -> authentication.getCredentials() instanceof String)
                .map(authentication -> (String) authentication.getCredentials());
    }

}
