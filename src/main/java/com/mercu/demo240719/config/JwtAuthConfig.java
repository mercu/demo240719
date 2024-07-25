package com.mercu.demo240719.config;

import com.mercu.demo240719.jwt.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class JwtAuthConfig {
    @Bean//(name = "")

    public JwtUtil jwtUtil() {
        return new JwtUtil("sklskljsklsjalkjklsjSKLSAKLJsklsklsjlksjsakljslkajsalksaksa",
                10 * 60 * 1000, 24 * 60 * 60 * 1000);
    }


}