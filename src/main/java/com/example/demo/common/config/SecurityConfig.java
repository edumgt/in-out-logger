package com.example.demo.common.config;

import com.example.demo.common.filter.JwtFilter;
import com.example.demo.model.SecretConfig;
import com.example.demo.service.auth.OAuth2UserService;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.security.Key;

@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final OAuth2UserService oAuth2UserService;


    @Bean
    public PasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public Key key(@Value("${jwt.secret}") String secretKey) {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(httpSecuritySessionManagementConfigurer -> {
                    httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                })
                .authorizeHttpRequests(authorizationManagerRequestMatcherRegistry -> {
                    authorizationManagerRequestMatcherRegistry.anyRequest().permitAll();
                })
                .addFilterBefore(new JwtFilter(), UsernamePasswordAuthenticationFilter.class)
                .oauth2Login(httpSecurityOAuth2LoginConfigurer -> {
                    httpSecurityOAuth2LoginConfigurer
//                            .loginPage("/login")
                            .userInfoEndpoint(userInfoEndpointConfig -> {
                                userInfoEndpointConfig.userService(oAuth2UserService);
                            });

                })
                .formLogin(AbstractHttpConfigurer::disable);
        return http.build();
    }

    @Bean
    @DependsOn("secretConfig")
    public ClientRegistrationRepository clientRegistrationRepository(SecretConfig secretConfig) {
        return new InMemoryClientRegistrationRepository(this.kakaoClientRegistration(secretConfig.getOauth2()));
    }

    private ClientRegistration kakaoClientRegistration(SecretConfig.OAuth2 oAuth2) {
        return ClientRegistration.withRegistrationId("kakao") // /oauth2/authorization/{withRegistrationId} url로 접근해서 인증 시작
                .clientId(oAuth2.getKakaoClient().getClientId())
                .clientSecret(oAuth2.getKakaoClient().getClientSecret())
                .authorizationUri("https://kauth.kakao.com/oauth/authorize")
                .tokenUri("https://kauth.kakao.com/oauth/token")
                .userInfoUri("https://kapi.kakao.com/v2/user/me")
                .userNameAttributeName("id")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
                .redirectUri("http://localhost/login/oauth2/code/kakao")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .scope("profile_nickname")
                .build();
    }
}
