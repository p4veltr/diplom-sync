package com.example.doplombackend.service;

import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthTokenService {
    private final JwtEncoder encoder;

    public AuthTokenService(JwtEncoder encoder) {
        this.encoder = encoder;
    }


}
