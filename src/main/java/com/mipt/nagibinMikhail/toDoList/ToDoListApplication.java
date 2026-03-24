package com.mipt.nagibinMikhail.toDoList;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * Главный класс приложения To-Do List Manager.
 * Запускает Spring Boot приложение и включает поддержку AOP.
 */
@SpringBootApplication
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class ToDoListApplication {
    public static void main(String[] args) {
        SpringApplication.run(ToDoListApplication.class, args);
    }
}
