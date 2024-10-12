package com.example.demo.controller;


import com.example.demo.common.constants.Token;
import com.example.demo.dto.request.JoinRequestDto;
import com.example.demo.dto.request.LoginRequestDto;
import com.example.demo.dto.token.TokenDto;
import com.example.demo.repository.user.UserRepository;
import com.example.demo.service.auth.AuthService;
import com.example.demo.service.auth.UserService;
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
    private final UserRepository userRepository;

    @PostMapping("/sign-in")
    public ResponseEntity<?> signIn(@RequestBody LoginRequestDto loginRequestDto) {
        TokenDto tokenDto = userService.signIn(loginRequestDto);
        return ResponseEntity.status(200)
                .header(Token.AUTHORIZATION_HEADER, Token.GRANT_TYPE + " " + tokenDto.getAccessToken())
                .body(tokenDto);
    }

    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(@RequestBody JoinRequestDto joinRequestDto) {
        userService.signUp(joinRequestDto);
        return ResponseEntity.status(201).build();
    }

    @GetMapping("/test")
    @Secured("ROLE_USER")
    public ResponseEntity<?> test() {
        var auth = authService.getAuthentication();

        Object principal = auth.getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            log.info("userDetails: {}", userDetails);
        }
        return ResponseEntity.status(200).build();
    }
}
