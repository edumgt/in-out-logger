package com.example.demo.calendar.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum VacationStatus {
    PENDING("보류"),
    REJECTED("반려"),
    APPROVED("승인");

    @JsonValue
    private final String value;
}
