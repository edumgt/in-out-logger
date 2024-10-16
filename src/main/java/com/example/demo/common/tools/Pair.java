package com.example.demo.common.tools;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Pair<F,S> {
    private final F first;
    private final S second;

    public static <F,S> Pair<F,S> of(F first, S second) {
        return new Pair<F,S>(first,second);
    }
}
