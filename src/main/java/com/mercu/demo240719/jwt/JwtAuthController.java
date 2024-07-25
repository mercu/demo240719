package com.mercu.demo240719.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercu.demo240719.user.User;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RequestMapping("/jwt")
@RestController
public class JwtAuthController {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private JwtAuthService jwtAuthService;
    @Autowired
    private ObjectMapper objectMapper;

    @Tag(name = "로그인 컨트롤러")
    @GetMapping("/authenticate")
    public ResponseEntity<String> authenticate(@RequestParam String username, @RequestParam String password) {
        try {
            //사용자 이름(아이디)과 비번으로 인증
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password));

            //인증 성공하면 JWT 토큰 생성
            //accessToken 생성
            String accessToken = jwtUtil.createAccessToken(User.builder()
                    .name(authentication.getName())
                    .build());
            //refreshToken 생성
            String refreshToken = jwtUtil.createRefreshToken(User.builder()
                    .name(authentication.getName())
                    .build());
            jwtAuthService.addRefreshToken(refreshToken, username);

            Map result = Map.of("access-token", accessToken,
                    "refresh-token", refreshToken);

            //생성된 토큰을 ResponseEntity로 반환
            return ResponseEntity.ok()
                    .body(objectMapper.writeValueAsString(result));
        } catch (UsernameNotFoundException | BadCredentialsException exception) {
            //사용자 이름이나 비번이 다른 경우 예외 처리
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid username or password");

            //todo 아이디 or 비번 따로따로 바꾸고 싶으면 catch 하나 더 주기
        } catch (Exception e) {
            log.error("authenticate failed! - username: {}, password: {}", username, password, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Exception : " + e.getMessage());
        }


    }

    @GetMapping("/refresh")
    public ResponseEntity<String> refresh(@RequestParam("refresh-token") String refreshToken) {
        try {
            String accessToken = jwtAuthService.refresh(refreshToken);
            //사용자 이름(아이디)과 비번으로 인증


            Map result = Map.of("access-token", accessToken,
                    "refresh-token", refreshToken);

            //생성된 토큰을 ResponseEntity로 반환
            return ResponseEntity.ok()
                    .body(objectMapper.writeValueAsString(result));
        } catch (Exception e) {
            log.error("refresh failed! - refresh-token: {}", refreshToken, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Exception : " + e.getMessage());
        }


    }

    @Tag(name = "로그아웃 컨트롤러")
    @GetMapping("/logout")
    public ResponseEntity<String> logout(@RequestParam("username") String username) {
        // 클라이언트 측에서 토큰을 삭제하도록 처리
//        response.setHeader("Set-Cookie", "accessToken=; HttpOnly; Path=/; Max-Age=0");
//        response.setHeader("Set-Cookie", "refreshToken=; HttpOnly; Path=/; Max-Age=0");
        jwtAuthService.logout(username);

        return ResponseEntity.ok().body("Logout Successful");
    }


}