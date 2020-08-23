package com.kd.novel.backend.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
@Lazy(false)
public class ApplicationContextHelper implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    public ApplicationContextHelper() {
    }

    public static Object getBean(String beanName) {
        return applicationContext != null ? applicationContext.getBean(beanName) : null;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ApplicationContextHelper.applicationContext = applicationContext;
    }
}