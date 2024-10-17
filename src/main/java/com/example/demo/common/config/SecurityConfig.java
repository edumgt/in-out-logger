package com.example.demo.common.config;

import com.example.demo.common.filter.JwtFilter;
import com.example.demo.common.handler.OAuth2SuccessHandler;
import com.example.demo.common.model.SecretConfig;
import com.example.demo.employee.service.OAuth2UserService;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
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

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {
    private final OAuth2UserService oAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    @Value("${oauth2.redirect-uri}")
    private String oauth2RedirectUri;

    @Bean
    public RoleHierarchy roleHierarchy(){
        return RoleHierarchyImpl.withDefaultRolePrefix()
                .role("CEO").implies("HEAD")
                .role("HEAD").implies("SENIOR_MANAGER")
                .role("SENIOR_MANAGER").implies("MANAGER")
                .role("MANAGER").implies("SENIOR_ASSOCIATE")
                .role("SENIOR_ASSOCIATE").implies("ASSOCIATE")
                .role("ASSOCIATE").implies("STAFF")
                .role("STAFF").implies("INTERN")
                .build();
    }


    @Bean
    public PasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public Key key(@Value("${jwt.secret}") String secretKey) {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtFilter jwtFilter) throws Exception {
        http.cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(httpSecuritySessionManagementConfigurer -> {
                    httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                })
                .authorizeHttpRequests(authorizationManagerRequestMatcherRegistry -> {
                    authorizationManagerRequestMatcherRegistry
                            .requestMatchers("/error", "/favicon.ico").permitAll()
                            .anyRequest().permitAll(); // 추후 변경, 또는 모든 권한 허가 후 메소드별 권한 제어
                })
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .oauth2Login(httpSecurityOAuth2LoginConfigurer -> {
                    httpSecurityOAuth2LoginConfigurer
                            .successHandler(oAuth2SuccessHandler)
                            .userInfoEndpoint(userInfoEndpointConfig -> {
                                userInfoEndpointConfig.userService(oAuth2UserService);
                            }); // loginPage -> /oauth2/authorization/{withRegistrationId}

                })
                .exceptionHandling(httpSecurityExceptionHandlingConfigurer -> {
                    httpSecurityExceptionHandlingConfigurer
                            .authenticationEntryPoint((request, response, authException) -> { // 401 핸들링
                                String uri = request.getRequestURI();
                                log.info("login failure uri {}", uri);
                                response.setStatus(401);
//                                if (uri.startsWith("/oauth2/authorization/")) {
//                                    response.sendRedirect("/oauth2/sign-in");
//                                } else {
//                                    response.sendRedirect("/sign-in");
//                                }
                            });
                });

        return http.build();
    }

    @Bean
    @DependsOn("secretConfig")
    public ClientRegistrationRepository clientRegistrationRepository(SecretConfig secretConfig) {
        return new InMemoryClientRegistrationRepository(kakaoClientRegistration(secretConfig.getOauth2()));
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
                .redirectUri(oauth2RedirectUri)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .scope("profile_nickname")
                .build();
    }
}
