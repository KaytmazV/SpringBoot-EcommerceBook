package com.volkankaytmaz.ebookcommerce.controller;

import com.volkankaytmaz.ebookcommerce.dto.LoginRequest;
import com.volkankaytmaz.ebookcommerce.dto.RegisterRequest;
import com.volkankaytmaz.ebookcommerce.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:63342", "http://localhost:8082"})
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        return authService.login(loginRequest);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        return authService.register(registerRequest);
    }
}

