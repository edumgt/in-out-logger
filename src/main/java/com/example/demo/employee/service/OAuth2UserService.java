package com.example.demo.employee.service;

import com.example.demo.common.enums.JobLevel;
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


    public OAuth2UserService(EmployeeRepository userRepository, @Lazy PasswordEncoder passwordEncoder) {
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
        String name = attributes.get("name").toString();
        String email = attributes.get("email").toString();

        String id = attributes.get(userNameAttributeName).toString();
        Assert.notNull(name, "cannot find name property");
        Assert.notNull(email, "cannot find email property");
        Assert.notNull(id, "cannot find id property");
        String hashedPassword = passwordEncoder.encode(id);
        Employee employee = employeeRepository.findByEmail(email).orElseGet(() -> {
            JobLevel jobLevel = JobLevel.ADMIN_EMAILS.getOrDefault(email, JobLevel.INTERN);
            Employee newEmployee = Employee.builder()
                    .name(name)
                    .email(email)
                    .jobLevel(jobLevel)
                    .password(hashedPassword)
                    .build();
            return employeeRepository.save(newEmployee);
        });
        return new PrincipalDetails(employee);
    }
}
