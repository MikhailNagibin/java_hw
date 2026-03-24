package com.mipt.nagibinMikhail.toDoList.config;


import com.mipt.nagibinMikhail.toDoList.repository.TaskRepository;
import com.mipt.nagibinMikhail.toDoList.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

/**
 * BeanPostProcessor для логирования жизненного цикла бинов.
 * Логирует создание и инициализацию бинов типа TaskService и TaskRepository.
 * Демонстрирует работу точек расширения Spring контейнера.
 *
 */
@Component
public class TaskLifecycleProcessor implements BeanPostProcessor {
    private static final Logger logger = LoggerFactory.getLogger(TaskLifecycleProcessor.class);

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof TaskService || bean instanceof TaskRepository) {
            logger.info("=== BeanPostProcessor: ДО инициализации бина [{}] класса {} ===",
                beanName, bean.getClass().getSimpleName());
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof TaskService || bean instanceof TaskRepository) {
            logger.info("=== BeanPostProcessor: ПОСЛЕ инициализации бина [{}] класса {} ===",
                beanName, bean.getClass().getSimpleName());
        }
        return bean;
    }
}
