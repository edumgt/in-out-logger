package com.example.demo.calendar.repository;

import com.example.demo.calendar.entity.Vacation;
import com.example.demo.employee.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface VacationRepository extends JpaRepository<Vacation, Long> {
    // 휴가 시작일과 종료일 사이에 휴가가 있으면 안된다
    @Query("""
            SELECT t FROM Vacation t WHERE t.createdBy = :createdBy
                AND ((t.start <= :start AND t.end >= :start)
                OR (t.start <= :end AND t.end >= :end)
                OR (t.start >= :start AND t.end <= :end))""")
    List<Vacation> findOverlappingVacations(@Param("createdBy") Employee createdBy,
                                            @Param("start") LocalDate start,
                                            @Param("end") LocalDate end);
}
