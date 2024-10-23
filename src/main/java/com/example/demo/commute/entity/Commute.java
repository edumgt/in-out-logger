package com.example.demo.commute.entity;

import com.example.demo.common.entity.CommonProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "commute")
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Commute extends CommonProperties {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date", nullable = false)
    @Comment("출퇴근 일자")
    private LocalDate date;

    @Column(name = "check_in_time")
    @Comment("출근 시각")
    private LocalTime checkInTime;

    @Column(name = "check_out_time")
    @Comment("퇴근 시각")
    private LocalTime checkOutTime;
}
