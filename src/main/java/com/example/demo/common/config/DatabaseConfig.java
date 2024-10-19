package com.example.demo.common.config;

import com.example.demo.employee.model.PrincipalDetails;
import com.example.demo.employee.entity.Employee;
import com.example.demo.common.model.SecretConfig;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.sql.DataSource;
import java.util.Optional;

@Configuration
@EnableJpaRepositories(basePackages = "com.example.demo.*.repository")
@EnableJpaAuditing
@RequiredArgsConstructor
public class DatabaseConfig {
//    @PersistenceContext
//    private EntityManager entityManager;
    @Bean
    public JPAQueryFactory jpaQueryFactory(EntityManager entityManager){
        return new JPAQueryFactory(entityManager);
    }
    @Bean
    @DependsOn("secretConfig")
    public DataSource dataSource(SecretConfig secretConfig) {
        SecretConfig.Database database = secretConfig.getDatabase();
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(database.getJdbcUrl());
        hikariConfig.setUsername(database.getUsername());
        hikariConfig.setPassword(database.getPassword());
        hikariConfig.setDriverClassName(database.getDriverClassName());
        return new HikariDataSource(hikariConfig);
    }


    @Bean
    public AuditorAware<Employee> auditorProvider() {
        return () -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if(authentication == null){
                return Optional.empty();
            }
            Object principal = authentication.getPrincipal();
            if(principal instanceof PrincipalDetails principalDetails) {
                return Optional.of(principalDetails.employee());
            }
            return Optional.empty();
        };
    }



}
