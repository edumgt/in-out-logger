package com.example.demo.service.auth;

import com.example.demo.common.auth.PrincipalDetails;
import com.example.demo.common.enums.LoginType;
import com.example.demo.common.enums.Role;
import com.example.demo.entity.User;
import com.example.demo.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Map;
import java.util.UUID;

@Service
public class OAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public OAuth2UserService(UserRepository userRepository,@Lazy PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();
        Map<String, String> properties = (Map<String, String>) attributes.get("properties");
        String username = properties.get("nickname");
        Long id = oAuth2User.getAttribute(userNameAttributeName); // provider마다 다르므로 "id" 값 고정이여서 안됨
        Assert.notNull(username, "cannot find username property");
        Assert.notNull(id, "cannot find id property");
        String hashedPassword = passwordEncoder.encode(id.toString());
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        User user = userRepository.findByEmail(registrationId + "$" + id).orElseGet(() -> {
            LoginType loginType = LoginType.from(registrationId);
            User newUser = User.builder()
                    .username(username)
                    .email(registrationId + "$" + id)
                    .password(hashedPassword)
                    .loginType(loginType)
                    .role(Role.ROLE_USER)
                    .build();
            return userRepository.save(newUser);
        });
        return new PrincipalDetails(user);
    }
}
