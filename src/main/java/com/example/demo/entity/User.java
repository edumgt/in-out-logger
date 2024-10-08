package com.example.demo.entity;

import com.example.demo.common.enums.LoginType;
import com.example.demo.common.enums.Role;
import com.example.demo.entity.common.CommonProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;

@Entity
@Table(name = "user")
@SuperBuilder
@Getter
@NoArgsConstructor
public class User extends CommonProperties implements Serializable {
    @Serial
    private static final long serialVersionUID = 2292704787934783396L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, columnDefinition = "varchar(50)")
    private String username;
    @Column(nullable = false, unique = true, columnDefinition = "varchar(50)")
    private String email;
    @Column(nullable = false, columnDefinition = "varchar(200)")
    private String password;
    @Column(nullable = false, columnDefinition = "varchar(30)")
    @Enumerated(EnumType.STRING)
    private Role role;
    @Column(name = "login_type")
    @Enumerated(EnumType.STRING)
    private LoginType loginType;


}
