package com.example.demo.common.aop;

import com.example.demo.common.annotation.MeasureTime;
import com.example.demo.common.enums.MeasureUnit;
import com.example.demo.common.utils.AopUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class LogAspect {

    @Pointcut("@annotation(com.example.demo.common.annotation.MeasureTime)")
    public void measureTimePointcut() {
    }

    @Around("measureTimePointcut()")
    public Object measureTime(ProceedingJoinPoint pjp) throws Throwable {
        MeasureTime measureTime = AopUtils.getAnnotation(pjp, MeasureTime.class);
        MeasureUnit unit = measureTime.unit();
        long start = System.currentTimeMillis();
        Object proceed = pjp.proceed();
        long end = System.currentTimeMillis();
        log.info("measure time : [{}] {}", end - start, unit.getString());
        return proceed;
    }
}
