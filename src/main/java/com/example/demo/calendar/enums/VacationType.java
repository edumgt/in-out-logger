package com.example.demo.calendar.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Getter
@RequiredArgsConstructor
public enum VacationType {
    ALL_DAY("종일 휴가"), // 1일 소모
    MORNING_HALF_DAY("오전 반차"), // 0.5일 소모
    AFTERNOON_HALF_DAY("오후 반차"), // 0.5일 소모
    SICK_LEAVE("병가"), // 0일 소모?
    UNPAID_LEAVE("무급 휴가"), // 0일 소모
    SPECIAL_LEAVE("특별 휴가"); // 0일 소모

    @JsonValue
    private final String value;

    private static final Set<VacationType> FREE_HOLDER = Set.of(SICK_LEAVE,UNPAID_LEAVE, SPECIAL_LEAVE);
    private static final Set<VacationType> HALF_HOLDER = Set.of(MORNING_HALF_DAY, AFTERNOON_HALF_DAY);
    private static final Map<String, VacationType> CONVERTER = new HashMap<>();
    static {
        for(VacationType vacationType : values()) {
            CONVERTER.put(vacationType.getValue(), vacationType);
        }
    }
    public static VacationType of(String value) {
        return CONVERTER.get(value);
    }
    public static boolean isHalf(VacationType vacationType) {
        return HALF_HOLDER.contains(vacationType);
    }
    public static boolean isFree(VacationType vacationType){
        return FREE_HOLDER.contains(vacationType);
    }
}
