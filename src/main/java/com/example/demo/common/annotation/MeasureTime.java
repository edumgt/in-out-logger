package com.example.demo.common.annotation;


import com.example.demo.common.enums.MeasureUnit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MeasureTime {

    MeasureUnit unit() default MeasureUnit.MILLI_SECOND;
}
