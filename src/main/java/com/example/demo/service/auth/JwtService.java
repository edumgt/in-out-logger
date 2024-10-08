package com.example.demo.service.auth;

import com.example.demo.dto.token.TokenDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JwtService {
    private static final String AUTHORITIES_KEY = "auth";
    private static final String GRANT_TYPE = "Bearer";
    private static final Long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 60L;
    private static final Long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 7L;
    private final Key key;
    private final HttpServletResponse response;
    private final HttpServletRequest request;

    public TokenDto generateToken(Authentication authentication) {

        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = new Date().getTime();

        Date tokenExpiresIn = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);


        String accessToken = Jwts.builder()
                .setSubject(authentication.getName())
                .claim(AUTHORITIES_KEY, authorities)
                .setExpiration(tokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
        String refreshToken = Jwts.builder()
//                .setSubject(authentication.getName())
                .claim(AUTHORITIES_KEY, authorities)
                .setExpiration(new Date(now + REFRESH_TOKEN_EXPIRE_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return TokenDto.builder()
                .grantType(GRANT_TYPE)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenExpiresIn(tokenExpiresIn.getTime())
                .build();
    }
    private Claims parseClaims(String accessToken) {
        return Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(accessToken).getBody();
    }
}
