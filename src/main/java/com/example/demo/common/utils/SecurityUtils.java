package com.example.demo.common.utils;

import com.example.demo.common.exception.HttpException;
import com.example.demo.employee.entity.Employee;
import com.example.demo.employee.model.PrincipalDetails;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

@UtilityClass
public class SecurityUtils {

    public Employee getCurrentUser() {
        Object principal = getAuthentication().getPrincipal();
        if(principal instanceof PrincipalDetails principalDetails){
            return principalDetails.employee();
        }
        throw new HttpException(HttpStatus.UNAUTHORIZED, "인증된 사용자가 아닙니다.");
    }
    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
