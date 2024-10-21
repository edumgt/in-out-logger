package com.example.demo.common.filter;

import com.example.demo.common.constants.Redis;
import com.example.demo.common.constants.Token;
import com.example.demo.common.service.redis.RedisService;
import com.example.demo.common.tools.Pair;
import com.example.demo.common.utils.HttpUtils;
import com.example.demo.employee.dto.token.TokenDto;
import com.example.demo.employee.service.AuthService;
import com.example.demo.employee.service.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final AuthService authService;
    private final RedisService redisService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = resolveToken(request);
        if (StringUtils.hasText(accessToken)) {
            Pair<Boolean, Claims> accessTokenPair = jwtService.validateToken(accessToken);
            if (accessTokenPair.getFirst()) {  // 액세스토큰 검사
                authService.setAuthentication(accessToken);
            } else {
                String clientIp = HttpUtils.getClientIp();
                String refreshToken = redisService.get(Redis.TOKEN_PREFIX + clientIp + accessToken, String.class);
                if (StringUtils.hasText(refreshToken)) { // 레디스에서 리프레시 토큰 조회
                    Pair<Boolean, Claims> refreshTokenPair = jwtService.validateToken(refreshToken);
                    if (refreshTokenPair.getFirst()) { // 조회 완료 후 검사
                        TokenDto tokenDto = jwtService.reissueToken(accessTokenPair.getSecond()); // 액세스토큰 / 리프레시토큰 재발급
                        authService.setAuthentication(tokenDto.getAccessToken());
                        redisService.set(Redis.TOKEN_PREFIX + clientIp + tokenDto.getAccessToken(), tokenDto.getRefreshToken(), Duration.ofMillis(Token.REFRESH_TOKEN_EXPIRE_TIME));
                        response.setHeader(Token.AUTHORIZATION_HEADER, Token.GRANT_TYPE + " " + tokenDto.getAccessToken());
                    }
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String token = request.getHeader(Token.AUTHORIZATION_HEADER);

        if (!StringUtils.hasText(token) || !token.startsWith(Token.GRANT_TYPE)) {
            return null;
        }
        return token.substring(Math.min(token.length(), 7));
    }
}
