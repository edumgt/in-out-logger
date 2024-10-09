package com.example.demo.service.auth;

import com.example.demo.dto.request.JoinRequestDto;
import com.example.demo.dto.request.LoginRequestDto;
import com.example.demo.dto.token.TokenDto;
import com.example.demo.entity.User;
import com.example.demo.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManagerBuilder managerBuilder;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Transactional(readOnly = true)
    public TokenDto signIn(LoginRequestDto loginRequestDto) {
        userRepository.findByEmail(loginRequestDto.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "유효하지 않은 아이디입니다."));
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginRequestDto.getEmail(),
                loginRequestDto.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        Authentication authentication = managerBuilder.getObject().authenticate(authenticationToken);
        return jwtService.generateToken(authentication);
    }

    public void checkUsername(String username) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 존재하는 유저명입니다.");
        }
    }

    public void checkEmail(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 존재하는 이메일입니다.");
        }
    }

    @Transactional
    public User signUp(JoinRequestDto joinRequestDto) {
        checkUsername(joinRequestDto.getUsername());
        checkEmail(joinRequestDto.getEmail());
        User user = User.builder()
                .email(joinRequestDto.getEmail())
                .password(joinRequestDto.getPassword())
                .username(joinRequestDto.getUsername())
                .build();
        return userRepository.save(user);
    }

}
