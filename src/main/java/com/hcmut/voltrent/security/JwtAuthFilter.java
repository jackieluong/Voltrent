package com.hcmut.voltrent.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthFilter.class);

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // Remove "Bearer "
            try {
                if (jwtSecret == null || jwtSecret.length() < 32) {
                    log.warn("jwt.secret missing or too short (must be >=32 chars) for token validation");
                    throw new IllegalStateException("Invalid server JWT configuration");
                }

                Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(key)
                        .build()
                        .parseClaimsJws(token)
                        .getBody();

                String userId = claims.getSubject();
                Object rolesObj = claims.get("roles");
                if (rolesObj == null) {
                    rolesObj = claims.get("role");
                }

                List<String> roles = new ArrayList<>();
                if (rolesObj instanceof List) {
                    ((List<?>) rolesObj).forEach(o -> {
                        if (o != null)
                            roles.add(o.toString());
                    });
                } else if (rolesObj instanceof String) {
                    String s = (String) rolesObj;
                    for (String part : s.split(",")) {
                        if (!part.isBlank())
                            roles.add(part.trim());
                    }
                }

                // Normalize to ROLE_ prefix (Spring Security expects ROLE_*)
                List<String> normalized = new ArrayList<>();
                for (String r : roles) {
                    if (r.startsWith("ROLE_"))
                        normalized.add(r);
                    else
                        normalized.add("ROLE_" + r.toUpperCase());
                }

                Collection<org.springframework.security.core.GrantedAuthority> authorities = normalized.stream()
                        .filter(Objects::nonNull)
                        .map(r -> (org.springframework.security.core.GrantedAuthority) () -> r)
                        .toList();

                // Set authentication
                var authentication = new UsernamePasswordAuthenticationToken(userId, token, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (Exception e) {
                log.debug("JWT validation failed: {}", e.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid or expired token");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}