package com.example.demo.controller;


import com.example.demo.dto.request.JoinRequestDto;
import com.example.demo.dto.request.LoginRequestDto;
import com.example.demo.dto.token.TokenDto;
import com.example.demo.service.auth.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Enumeration;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/sign-in")
    public ResponseEntity<?> signIn(@RequestBody LoginRequestDto loginRequestDto) {
        TokenDto tokenDto = authService.signIn(loginRequestDto);
        return ResponseEntity.ok(tokenDto);
    }

    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(@RequestBody JoinRequestDto joinRequestDto, HttpServletRequest request) {
        Enumeration<String> enumeration = request.getHeaderNames();
        while (enumeration.hasMoreElements()) {
            String header = enumeration.nextElement();
            log.info("header {} : {} ",header, request.getHeader(header));
        }
        authService.signUp(joinRequestDto);
        return ResponseEntity.status(201).build();
    }
}
