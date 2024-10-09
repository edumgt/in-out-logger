package com.example.demo.service.auth;

import com.example.demo.common.auth.PrincipalDetails;
import com.example.demo.entity.User;
import com.example.demo.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow();
        // 이거 명시적 호출제외하고 다형성으로 호출되는지 체크
        return new PrincipalDetails(user);
    }
}
