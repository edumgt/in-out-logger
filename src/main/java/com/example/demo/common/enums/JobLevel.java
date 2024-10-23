package com.example.demo.common.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Getter
@RequiredArgsConstructor
public enum JobLevel {
    CEO("대표"),
    HEAD("실장"),
    SENIOR_MANAGER("차장"),
    MANAGER("과장"),
    SENIOR_ASSOCIATE("대리"),
    ASSOCIATE("주임"),
    STAFF("사원"),
    INTERN("인턴");
    @JsonValue
    private final String value;


    public static final String ROLE_PREFIX = "ROLE_";
    public static final Set<JobLevel> ADMIN_LEVEL = Set.of(CEO, HEAD);
    public static final Map<String, JobLevel> ADMIN_EMAILS = Map.of(
            "jenkis@jeit.co.kr", JobLevel.CEO,
            "gm@jeit.co.kr", JobLevel.HEAD
    );
    private static final Map<String, JobLevel> CONVERTER = new HashMap<>();

    static {
        for (JobLevel jobLevel : values()) {
            CONVERTER.put(ROLE_PREFIX + jobLevel.name(), jobLevel);
        }
    }

    public static JobLevel of(String name) {
        if (name.startsWith(ROLE_PREFIX)) {
            return CONVERTER.get(name.toUpperCase());
        }
        return CONVERTER.get(ROLE_PREFIX + name.toUpperCase());
    }

    public static String getRole(JobLevel jobLevel) {
        return ROLE_PREFIX + jobLevel.name();
    }
}
