package com.example.demo.service.auth;

import com.example.demo.common.enums.Role;
import com.example.demo.common.exception.HttpException;
import com.example.demo.dto.request.JoinRequestDto;
import com.example.demo.dto.request.LoginRequestDto;
import com.example.demo.dto.token.TokenDto;
import com.example.demo.entity.User;
import com.example.demo.repository.user.UserRepository;
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

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final AuthenticationManagerBuilder managerBuilder;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public TokenDto signIn(LoginRequestDto loginRequestDto) {
        User user = userRepository.findByEmail(loginRequestDto.getEmail())
                .orElseThrow(() -> new HttpException(HttpStatus.BAD_REQUEST, "유효하지 않은 아이디입니다."));
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginRequestDto.getEmail(),
                loginRequestDto.getPassword(),
                List.of(new SimpleGrantedAuthority(user.getRole().name()))
        );
        try {
            Authentication authentication = managerBuilder.getObject().authenticate(authenticationToken);
            return jwtService.generateToken(authentication);
        } catch (BadCredentialsException e){
            throw new HttpException(HttpStatus.BAD_REQUEST, "유효하지 않은 비밀번호입니다.");
        } catch (Exception e){
            log.error(e.getMessage());
            throw new HttpException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }

    }

    public void checkUsername(String username) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isPresent()) {
            throw new HttpException(HttpStatus.CONFLICT, "이미 존재하는 닉네임입니다.");
        }
    }

    public void checkEmail(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            throw new HttpException(HttpStatus.CONFLICT, "이미 존재하는 이메일입니다.");
        }
    }

    @Transactional
    public User signUp(JoinRequestDto joinRequestDto) {
        checkUsername(joinRequestDto.getUsername());
        checkEmail(joinRequestDto.getEmail());
        String hashedPassword = passwordEncoder.encode(joinRequestDto.getPassword());
        User user = User.builder()
                .email(joinRequestDto.getEmail())
                .password(hashedPassword)
                .username(joinRequestDto.getUsername())
                .role(Role.ROLE_USER)
                .build();
        return userRepository.save(user);
    }

}
