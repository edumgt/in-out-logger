package com.example.demo.employee.service;

import com.example.demo.common.constants.Redis;
import com.example.demo.common.constants.Token;
import com.example.demo.common.enums.EmploymentStatus;
import com.example.demo.common.enums.JobLevel;
import com.example.demo.common.exception.HttpException;
import com.example.demo.common.service.redis.RedisService;
import com.example.demo.common.utils.HttpUtils;
import com.example.demo.employee.dto.request.LoginRequestDto;
import com.example.demo.employee.dto.request.SignUpRequestDto;
import com.example.demo.employee.dto.token.TokenDto;
import com.example.demo.employee.entity.Employee;
import com.example.demo.employee.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final AuthenticationManagerBuilder managerBuilder;
    private final EmployeeRepository employeeRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final RedisService redisService;

    // 일반 로그인
    public TokenDto signIn(LoginRequestDto loginRequestDto) {
        Employee employee = employeeRepository.findByEmail(loginRequestDto.getEmail())
                .orElseThrow(() -> new HttpException(HttpStatus.BAD_REQUEST, "유효하지 않은 아이디입니다."));
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginRequestDto.getEmail(),
                loginRequestDto.getPassword(),
                List.of(new SimpleGrantedAuthority(JobLevel.getRole(employee.getJobLevel())))
        );
        try {
            Authentication authentication = managerBuilder.getObject().authenticate(authenticationToken);
            String clientIp = HttpUtils.getClientIp();
            TokenDto tokenDto = jwtService.generateToken(authentication);
            redisService.set(Redis.TOKEN_PREFIX + clientIp + tokenDto.getAccessToken(), tokenDto.getRefreshToken(), Duration.ofMillis(Token.REFRESH_TOKEN_EXPIRE_TIME));
            return tokenDto;
        } catch (BadCredentialsException e) {
            throw new HttpException(HttpStatus.BAD_REQUEST, "유효하지 않은 비밀번호입니다.");
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new HttpException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }

    }


    public void checkEmail(String email) {
        Optional<Employee> optionalEmployee = employeeRepository.findByEmail(email);
        if (optionalEmployee.isPresent()) {
            throw new HttpException(HttpStatus.CONFLICT, "이미 존재하는 이메일입니다.");
        }
    }

    @Transactional
    public Employee signUp(SignUpRequestDto signUpRequestDto) {
        checkEmail(signUpRequestDto.getEmail());
        String hashedPassword = passwordEncoder.encode(signUpRequestDto.getPassword());
        Employee user = Employee.builder()
                .email(signUpRequestDto.getEmail())
                .password(hashedPassword)
                .name(signUpRequestDto.getName())
                .jobLevel(JobLevel.INTERN)
                .hireDate(LocalDate.now())
                .phoneNumber("010-4544-7362")
                .employmentStatus(EmploymentStatus.CONTRACT)
                .build();
        return employeeRepository.save(user);
    }

}
