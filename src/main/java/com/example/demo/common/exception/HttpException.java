package com.example.demo.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public class HttpException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final String message;

}
