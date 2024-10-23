package com.example.demo.common.utils;

import com.example.demo.common.enums.JobLevel;
import com.example.demo.common.exception.HttpException;
import com.example.demo.employee.entity.Employee;
import com.example.demo.employee.model.PrincipalDetails;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Objects;

@UtilityClass
public class SecurityUtils {
    /**
     * 현재 로그인된 유저와 id 값 비교하여 일치 또는 관리자일 경우 허용
     *
     * @param employeeId
     */
    public void checkPermission(Long employeeId) {
        Employee currentUser = getCurrentUser();
        if (!Objects.equals(currentUser.getId(), employeeId) && !isAdmin(currentUser)) {
            throw new HttpException(HttpStatus.FORBIDDEN, "권한이 없습니다.");
        }
    }

    public boolean isAdmin(Employee employee) {
        return JobLevel.ADMIN_LEVEL.contains(employee.getJobLevel());
    }
    public boolean isAdmin(JobLevel jobLevel) {
        return JobLevel.ADMIN_LEVEL.contains(jobLevel);
    }

    public Employee getCurrentUser() {
        Object principal = getAuthentication().getPrincipal();
        if (principal instanceof PrincipalDetails principalDetails) {
            return principalDetails.employee();
        }
        throw new HttpException(HttpStatus.UNAUTHORIZED, "인증된 사용자가 아닙니다.");
    }

    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
