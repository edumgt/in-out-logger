package com.example.demo.employee.model;

import com.example.demo.common.enums.JobLevel;
import com.example.demo.employee.entity.Employee;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@Slf4j
public record PrincipalDetails(Employee employee) implements OAuth2User, UserDetails, Serializable {
    @Serial
    private static final long serialVersionUID = 6036051638701599658L;

    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(JobLevel.getRole(employee.getJobLevel())));
    }

    @Override
    public String getPassword() {
        return employee.getPassword();
    }

    @Override
    public String getUsername() {
        return employee.getName();
    }


    @Override
    public String getName() {
        return employee.getEmail();
    }
}

