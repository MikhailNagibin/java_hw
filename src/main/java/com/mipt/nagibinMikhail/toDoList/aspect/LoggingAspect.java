package com.mipt.nagibinMikhail.toDoList.aspect;


import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Аспект для логирования выполнения методов сервисов.
 * Использует @Around advice для логирования начала, конца и результатов выполнения методов.
 * Демонстрирует возможности AOP в Spring.
 *
 * @author Student Name
 * @version 1.0
 */
@Aspect
@Component
public class LoggingAspect {
    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    /**
     * Pointcut для всех методов в пакете service и его подпакетах
     */
    @Pointcut("execution(* com.todolist.service.*.*(..))")
    public void serviceMethods() {}

    /**
     * Around advice для логирования выполнения методов сервисов
     */
    @Around("serviceMethods()")
    public Object logMethodExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String className = signature.getDeclaringType().getSimpleName();
        String methodName = signature.getName();
        Object[] args = joinPoint.getArgs();

        logger.info("=== AOP: НАЧАЛО выполнения {}.{}() ===", className, methodName);
        logger.info("Параметры: {}", args.length > 0 ? Arrays.toString(args) : "нет параметров");

        long startTime = System.currentTimeMillis();
        Object result = null;

        try {
            result = joinPoint.proceed();
            long endTime = System.currentTimeMillis();

            logger.info("=== AOP: ЗАВЕРШЕНИЕ {}.{}() ===", className, methodName);

            if (result != null) {
                logger.info("Результат: {}", result);
            } else {
                logger.info("Результат: void (метод ничего не возвращает)");
            }

            logger.info("Время выполнения: {} мс", (endTime - startTime));

            return result;

        } catch (Throwable throwable) {
            logger.error("=== AOP: ОШИБКА в {}.{}() ===", className, methodName);
            logger.error("Тип ошибки: {}", throwable.getClass().getSimpleName());
            logger.error("Сообщение: {}", throwable.getMessage());
            throw throwable;
        }
    }
}
