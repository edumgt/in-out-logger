package com.example.demo.common.constants;

public class Token {
    public static final String AUTHORITIES_KEY = "role";
    public static final String GRANT_TYPE = "Bearer";
    public static final Long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 60L;
    public static final Long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 7L;
    public static final String AUTHORIZATION_HEADER = "Authorization";
}
