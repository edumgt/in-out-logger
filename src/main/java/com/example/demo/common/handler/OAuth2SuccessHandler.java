package com.example.demo.common.handler;

import com.example.demo.dto.token.TokenDto;
import com.example.demo.service.auth.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {
    private final JwtService jwtService;

    public OAuth2SuccessHandler(@Lazy JwtService jwtService) {
        this.jwtService = jwtService;
    }
    @Value("${frontend.uri}")
    private String frontendUri;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        TokenDto tokenDto = jwtService.generateToken(authentication);
        String redirectUrl = UriComponentsBuilder.fromUriString(frontendUri)
                .queryParam("token", tokenDto.getAccessToken())
                .queryParam("exp", tokenDto.getTokenExpiresIn())
                .queryParam("username", authentication.getName())
                .build().toUriString();

        response.sendRedirect(redirectUrl);
    }
}
