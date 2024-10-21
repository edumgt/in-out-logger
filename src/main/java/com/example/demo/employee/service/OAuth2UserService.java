package com.example.demo.employee.service;

import com.example.demo.common.enums.LoginType;
import com.example.demo.employee.entity.Employee;
import com.example.demo.employee.model.PrincipalDetails;
import com.example.demo.employee.repository.EmployeeRepository;
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

@Service
public class OAuth2UserService extends DefaultOAuth2UserService {
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    public OAuth2UserService(EmployeeRepository userRepository,@Lazy PasswordEncoder passwordEncoder) {
        this.employeeRepository = userRepository;
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
        Employee employee = employeeRepository.findByEmail(registrationId + "$" + id).orElseGet(() -> {
            LoginType loginType = LoginType.nameOf(registrationId);
            Employee newEmployee = Employee.builder()
                    .name(username)
                    .password(hashedPassword)
                    .build();
            return employeeRepository.save(newEmployee);
        });
        return new PrincipalDetails(employee);
    }
}
