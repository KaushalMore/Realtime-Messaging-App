package com.more.chat.controller;

import com.more.chat.dto.LoginRequest;
import com.more.chat.dto.Response;
import com.more.chat.dto.UserRegistrationDto;
import com.more.chat.entity.User;
import com.more.chat.jwt.JwtService;
import com.more.chat.service.interfaces.AuthService;
import com.more.chat.service.interfaces.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {

    private final UserService userService;
    private final AuthService authService;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<Response> register(@Valid @RequestBody UserRegistrationDto userDto) {
        Response response = userService.saveUser(userDto);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<Response> login(@Valid @RequestBody LoginRequest loginRequest) {
        Response response = authService.verify(loginRequest);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String auth) {
        String email = jwtService.extractUsername(auth.substring(7));
        authService.disconnectUser(email);
        return ResponseEntity.ok("Logout");
    }
}