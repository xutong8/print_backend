package com.zju.vis.print_backend.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@Aspect
@RestControllerAdvice
public class ControllerTimeAdvice {

    // 匹配控制层的全部方法
    @Pointcut("execution(* com.zju.vis.print_backend.controller.*Controller.*(..))")
    private void timeUsePt(){}

    // 因为不知道会不会出异常，强制加一个可抛出异常
    @Around("timeUsePt()")
    public Object timeUse(ProceedingJoinPoint pjp) throws Throwable{
        // 一次执行的签名信息
        Signature signature = pjp.getSignature();
        String className = signature.getDeclaringTypeName();
        String methodName = signature.getName();
        long s = System.currentTimeMillis();
        // 表示对原始操作的调用
        Object ret = pjp.proceed();
        long e = System.currentTimeMillis();

        log.info("执行函数：" + className+"."+methodName+signature.getName()+" ----> "+(e - s) + " ms");
        // 返回原始方法的返回值
        return ret;
    }
}
