package com.example.demo.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JoinRequestDto {
    private String username;
    private String email;
    private String password;
    private String confirmPassword;
}
