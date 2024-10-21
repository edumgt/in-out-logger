package com.example.demo.common.enums;

import java.util.HashMap;
import java.util.Map;

public enum Role {
    ROLE_USER,
    ROLE_ADMIN;

    private static final Map<String, Role> CONVERTER = new HashMap<>();

    static {
        for(Role role : values()) {
            CONVERTER.put(role.name(), role);
        }
    }

    public static Role nameOf(String name){
        return CONVERTER.get(name.toUpperCase());
    };
}

