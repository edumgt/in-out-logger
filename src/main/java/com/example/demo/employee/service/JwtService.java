package com.example.demo.employee.service;

import com.example.demo.common.constants.Token;
import com.example.demo.common.tools.Pair;
import com.example.demo.employee.dto.token.TokenDto;
import com.example.demo.employee.model.PrincipalDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {

    private final Key key;

    public TokenDto generateToken(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        long now = new Date().getTime();
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof PrincipalDetails principalDetails)) { // 인증을 거쳤을 때에만 토큰 생성 jwtFilter 거칠 시 UserDetails 반환 -> 이제 거쳐도 PrincipalDetails 반환되게끔 수정함
            throw new RuntimeException();
        }
        Long id = principalDetails.employee().getId(); // stateless 해야하기 때문에 DB 조회하지 않음 -> AuditorAware에서 조회할 수 있게끔 id값 넣어줌
        Date tokenExpiresIn = new Date(now + Token.ACCESS_TOKEN_EXPIRE_TIME);
        String accessToken = Jwts.builder()
                .setSubject(principalDetails.getName())
                .claim(Token.AUTHORITIES_KEY, authorities)
                .claim("id", id)
                .claim("name", principalDetails.getUsername())
                .claim("email", principalDetails.employee().getEmail())
                .setExpiration(tokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
        String refreshToken = Jwts.builder()
                .setExpiration(new Date(now + Token.REFRESH_TOKEN_EXPIRE_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return TokenDto.builder()
                .grantType(Token.GRANT_TYPE)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenExpiresIn(tokenExpiresIn.getTime())
                .username(principalDetails.getUsername())
                .email(principalDetails.employee().getEmail())
                .build();
    }
    public TokenDto reissueToken(Claims claims) {
        if(claims == null){
            throw new IllegalArgumentException("Claims cannot be null");
        }
        String authorities = claims.get(Token.AUTHORITIES_KEY, String.class);
        Long id = claims.get("id", Long.class);
        String sub = claims.get("sub", String.class);
        String name = claims.get("name", String.class);
        long now = new Date().getTime();
        Date tokenExpiresIn = new Date(now + Token.ACCESS_TOKEN_EXPIRE_TIME);
        String accessToken = Jwts.builder()
                .setSubject(sub)
                .claim(Token.AUTHORITIES_KEY, authorities)
                .claim("id", id)
                .claim("name", name)
                .setExpiration(tokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
        String refreshToken = Jwts.builder()
                .setExpiration(new Date(now + Token.REFRESH_TOKEN_EXPIRE_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return TokenDto.builder()
                .grantType(Token.GRANT_TYPE)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenExpiresIn(tokenExpiresIn.getTime())
                .username(name)
                .build();
    }

    public Pair<Boolean, Claims> validateToken(String token) {
        if (!StringUtils.hasText(token)) {
            return Pair.of(false, null);
        }
        try {
            Claims claims = parseClaims(token);
            return Pair.of(claims.getExpiration().after(new Date()), null);
        } catch (ExpiredJwtException e) {
            return Pair.of(false, e.getClaims());
        } catch (Exception e) {
            return Pair.of(false, null);
        }
    }

    public Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

}
