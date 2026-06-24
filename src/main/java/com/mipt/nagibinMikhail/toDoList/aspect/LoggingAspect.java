package com.mipt.nagibinMikhail.toDoList.aspect;

import com.mipt.nagibinMikhail.toDoList.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class LoggingAspect {

    private final JwtUtils jwtUtils;

    @Pointcut("within(com.mipt.nagibinMikhail.toDoList.service..*)")
    public void serviceLayer() {}

    @Pointcut("within(com.mipt.nagibinMikhail.toDoList.client..*)")
    public void clientLayer() {}

    @Around("serviceLayer() || clientLayer()")
    public Object logMethodCall(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        String traceId = MDC.get("traceId");

        // Маскируем чувствительные аргументы
        Object[] args = joinPoint.getArgs();
        String maskedArgs = maskSensitiveArgs(args);

        log.debug("Calling {} with args: {} trace={}", methodName, maskedArgs, traceId);

        long startTime = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            long duration = System.currentTimeMillis() - startTime;

            log.debug("Completed {} in {}ms trace={}", methodName, duration, traceId);
            return result;

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("Error in {} after {}ms: {} trace={}",
                methodName, duration, e.getMessage(), traceId);
            throw e;
        }
    }

    private String maskSensitiveArgs(Object[] args) {
        if (args == null || args.length == 0) {
            return "[]";
        }

        return Arrays.toString(Arrays.stream(args)
            .map(arg -> {
                if (arg == null) return "null";
                String argStr = arg.toString();
                // Маскируем пароли и токены
                if (argStr.contains("password") || argStr.contains("token")) {
                    return "[MASKED]";
                }
                return argStr;
            })
            .toArray());
    }
}
