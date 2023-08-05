package com.example.doplombackend.controller;

import com.example.doplombackend.model.auth.LoginData;
import com.example.doplombackend.model.auth.LoginRespond;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class Controller {
    public final AuthenticationManager authenticationManager;

    public Controller(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/login")
    public LoginRespond login(@RequestBody LoginData loginData) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginData.login(),
                loginData.password()));
        return new LoginRespond(authentication.toString());
    }
}
