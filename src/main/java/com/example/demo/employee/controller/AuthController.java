package com.example.demo.employee.controller;


import com.example.demo.common.constants.Token;
import com.example.demo.common.utils.SecurityUtils;
import com.example.demo.employee.dto.request.LoginRequestDto;
import com.example.demo.employee.dto.request.SignUpRequestDto;
import com.example.demo.employee.dto.token.TokenDto;
import com.example.demo.employee.service.AuthService;
import com.example.demo.employee.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final AuthService authService;

    @PostMapping("/sign-in")
    public ResponseEntity<?> signIn(@RequestBody LoginRequestDto loginRequestDto) {
        TokenDto tokenDto = userService.signIn(loginRequestDto);
        return ResponseEntity.status(200)
                .header(Token.AUTHORIZATION_HEADER, Token.GRANT_TYPE + " " + tokenDto.getAccessToken())
                .body(tokenDto);
    }

    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(@RequestBody SignUpRequestDto signUpRequestDto) {
        userService.signUp(signUpRequestDto);
        return ResponseEntity.status(201).build();
    }

    @GetMapping("/test")
    @Secured("ROLE_INTERN")
    public ResponseEntity<?> test() {
        var auth = SecurityUtils.getAuthentication();
        Object principal = auth.getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            log.info("userDetails: {}", userDetails);
        }
        return ResponseEntity.status(200).build();
    }
}
