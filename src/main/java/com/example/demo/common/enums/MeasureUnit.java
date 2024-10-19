package com.example.demo.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MeasureUnit {
    MILLI_SECOND("ms"),
    NANO_SECOND("ns");

    private final String string;
}
