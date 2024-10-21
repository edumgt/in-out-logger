package com.example.demo.calendar.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Getter
@RequiredArgsConstructor
public enum VacationType {
    ALL_DAY("종일 휴가"),
    MORNING_HALF_DAY("오전 반차"),
    AFTERNOON_HALF_DAY("오후 반차"),
    SICK_LEAVE("병가"),
    UNPAID_LEAVE("무급 휴가"),
    SPECIAL_LEAVE("특별 휴가");

    private final String description;
    private static final Set<VacationType> HALF_HOLDER = Set.of(VacationType.MORNING_HALF_DAY, VacationType.AFTERNOON_HALF_DAY);
    private static final Map<String, VacationType> CONVERTER = new HashMap<>();
    static {
        for(VacationType vacationType : values()) {
            CONVERTER.put(vacationType.getDescription(), vacationType);
        }
    }
    public static VacationType descriptionOf(String description) {
        return CONVERTER.get(description);
    }
    public static boolean isHalf(VacationType vacationType) {
        return HALF_HOLDER.contains(vacationType);
    }
}
