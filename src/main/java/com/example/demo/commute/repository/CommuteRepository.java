package com.example.demo.commute.repository;

import com.example.demo.commute.entity.Commute;
import com.example.demo.employee.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface CommuteRepository extends JpaRepository<Commute, Long> {

    Optional<Commute> findByCreatedByAndDate(Employee createdBy, LocalDate date);

}
