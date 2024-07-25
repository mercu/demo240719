package com.mercu.demo240719.jwt;

import com.mercu.demo240719.config.security.MyUserDetails;
import com.mercu.demo240719.user.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

//JWT 관련 메소드를 제공하는 클래스
//JWT 생성, 검증, 추출 등의 기능을 수행한다.
@Slf4j

//@Component
@RequiredArgsConstructor
public class JwtUtil {

    private Key key; //JWT 서명을 위한 시크릿 키
    private long accessTokenExpTime; //AccessToken 의 만료 시간
    private long refreshTokenExpTime;

    public JwtUtil(
//            @Value("${jwt.secret}") String secretKey,
//            @Value("${jwt.expiration_time}") long accessTokenExpTime

            //secretKey 와 accessTokenExpTime 을 받아옴
            String secretKey, long accessTokenExpTime, long refreshTokenExpTime
    ) {
        // 받아온 객체로 키를 설정(base64 로 기본키로 설정)
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        //만료 시간 초기화.
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenExpTime = accessTokenExpTime;
        this.refreshTokenExpTime = refreshTokenExpTime;
    }


    //UserEntity를 바탕으로 AccessToken을 생성함.
    public String createAccessToken(User user) {
        return createToken(user, accessTokenExpTime);
    }

    public String createRefreshToken(User user) {
        return createToken(user, refreshTokenExpTime);
    }

    //JWT 생성
    private String createToken(User user, long expireTime) {
        //JWT Claims에 사용자 정보와 권한 저장
        Claims claims = Jwts.claims();
        claims.put("userId", user.getName());
        claims.put("email", user.getEmail());
        claims.put("role", "ROLE_USER"); // TODO

        //만료 시간 설정
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime tokenValidity = now.plusSeconds(expireTime);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(Date.from(now.toInstant()))
                .setExpiration(Date.from(tokenValidity.toInstant()))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    //토큰에서 사용자 아이디 추출
    public Long getUserId(String token) {
        return parseClaims(token).get("userId", Long.class);
    }

    //토큰을 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e);
        }
        return false;
    }


    //JWT에서 Claims를 추출
    public Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    //Authorization 헤더에서 토큰 추출
    public String extractTokenFromHeader(String authorization) {
        if (StringUtils.isEmpty(authorization) || !authorization.startsWith("Bearer")) {
            return null;
        }
        return authorization.replaceAll("Bearer", "").trim();
    }

    // Access Token에 들어있는 정보를 꺼내 Authentication 객체를 생성 후 반환한다.
    public Authentication getAuthentication(String accessToken) {
        // 토큰의 Payload에 저장된 Claim들을 추출한다.
        Claims claims = parseClaims(accessToken);

        if (claims.get("role") == null) {
            // 권한 정보 없는 토큰
            throw new AuthenticationServiceException("no exist role!!");
        }

        // Claim에서 권한 정보를 추출한다.
        Collection<? extends GrantedAuthority> authorities = Arrays
                .stream(claims.get("role").toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        // Claim에 저장된 사용자 아이디를 통해 UserDetails 객체를 생성한다.
        UserDetails principal = MyUserDetails.create((String)claims.get("memberId"), null, authorities);

        // Authentication 객체를 생성하여 반환한다.
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }
}