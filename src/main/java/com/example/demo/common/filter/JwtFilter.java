package com.example.demo.common.filter;

import com.example.demo.common.constants.Token;
import com.example.demo.service.auth.AuthService;
import com.example.demo.service.auth.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final AuthService authService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = resolveToken(request);
        if(jwtService.validateToken(accessToken)){
            Authentication authentication = authService.createAuthentication(accessToken);
            authService.setAuthentication(authentication);
        } else {
            // refreshToken으로 재발급
        }


        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String token = request.getHeader(Token.AUTHORIZATION_HEADER);
        if (!StringUtils.hasText(token) || !token.startsWith(Token.GRANT_TYPE)) {
            return null;
        }
        return token.substring(Math.min(token.length(),7));
    }
}
