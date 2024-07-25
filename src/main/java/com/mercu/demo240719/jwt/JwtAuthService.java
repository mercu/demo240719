package com.mercu.demo240719.jwt;

import com.mercu.demo240719.user.User;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class JwtAuthService {
    @Autowired
    private JwtUtil jwtUtil;
    private Map<String, String> refreshTokenMap = new HashMap<>();
    private Map<String, String> usernameTokenMap = new HashMap<>();

    public void addRefreshToken(String refreshToken, String username) {
        refreshTokenMap.put(refreshToken, username);
        usernameTokenMap.put(username, refreshToken);
    }

    public String refresh(String refreshToken) {
        if (!jwtUtil.validateToken(refreshToken) || Objects.isNull(refreshTokenMap.get(refreshToken))) {
            throw new AuthenticationException("invalid refresh-token!") {
            };
        }

        Claims claims = jwtUtil.parseClaims(refreshToken);

        //accessToken 생성
        String accessToken = jwtUtil.createAccessToken(User.builder()
                .name(claims.get("userId", String.class))
                .build());

        return accessToken;
    }

    public void logout(String username) {
        String refreshToken = usernameTokenMap.get(username);
        refreshTokenMap.remove(refreshToken);
        usernameTokenMap.remove(username);
    }
}