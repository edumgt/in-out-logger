package com.example.demo.common.enums;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public enum JobLevel {
    CEO, // 대표
    HEAD, // 실장
    SENIOR_MANAGER, // 차장
    MANAGER, // 과장
    SENIOR_ASSOCIATE, // 대리
    ASSOCIATE, // 주임
    STAFF, // 사원
    INTERN; // 인턴
    public static final Set<JobLevel> ADMIN_LEVEL = Set.of(JobLevel.CEO, JobLevel.HEAD);
    public static final Map<String, JobLevel> ADMIN_EMAILS = Map.of(
            "jenkis@jeit.co.kr", JobLevel.CEO,
            "gm@jeit.co.kr", JobLevel.HEAD
    );
    private static final Map<String, JobLevel> CONVERTER = new HashMap<>();
    static {
        for(JobLevel jobLevel : values()) {
            CONVERTER.put(jobLevel.name() , jobLevel);
        }
    }
    public static JobLevel nameOf(String name) {
        return CONVERTER.get(name.toUpperCase());
    }
    public static String getRole(JobLevel jobLevel) {
        return "ROLE_" + jobLevel.name();
    }
}
