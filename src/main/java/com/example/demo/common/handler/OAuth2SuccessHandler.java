package com.example.demo.common.handler;

import com.example.demo.common.constants.Redis;
import com.example.demo.common.constants.Token;
import com.example.demo.common.utils.HttpUtils;
import com.example.demo.dto.token.TokenDto;
import com.example.demo.service.auth.JwtService;
import com.example.demo.service.redis.RedisService;
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
import java.time.Duration;

@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {
    private final JwtService jwtService;
    private final RedisService redisService;

    public OAuth2SuccessHandler(@Lazy JwtService jwtService, RedisService redisService) {
        this.jwtService = jwtService;
        this.redisService = redisService;
    }
    @Value("${frontend.uri}")
    private String frontendUri;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        TokenDto tokenDto = jwtService.generateToken(authentication);
        String clientIp = HttpUtils.getClientIp();
        redisService.set(Redis.TOKEN_PREFIX + clientIp + tokenDto.getAccessToken(), tokenDto.getRefreshToken(), Duration.ofMillis(Token.REFRESH_TOKEN_EXPIRE_TIME));
        String redirectUrl = UriComponentsBuilder.fromUriString(frontendUri)
                .queryParam("token", tokenDto.getAccessToken())
                .queryParam("username", authentication.getName())
                .encode()
                .build()
                .toUriString();

        response.sendRedirect(redirectUrl);
    }
}
