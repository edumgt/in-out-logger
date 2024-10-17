package com.example.demo.commute.repository;

import com.example.demo.commute.entity.Commute;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommuteRepository extends JpaRepository<Commute, Long> {
}
