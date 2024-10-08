package com.example.demo.controller;


import com.example.demo.dto.request.JoinRequestDto;
import com.example.demo.dto.request.LoginRequestDto;
import com.example.demo.dto.token.TokenDto;
import com.example.demo.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/auth")
@RequiredArgsConstructor
@RestController
public class AuthController {
    private final AuthService authService;


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginRequestDto){
        TokenDto tokenDto = authService.login(loginRequestDto);
        return ResponseEntity.ok(tokenDto);
    }

    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestBody JoinRequestDto joinRequestDto){
        authService.join(joinRequestDto);
        return ResponseEntity.status(201).build();
    }
}
