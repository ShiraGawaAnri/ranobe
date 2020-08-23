package com.kd.novel.backend.config;

import org.hibernate.validator.HibernateValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

@Configuration
public class ValidatorConfigBean {

    @Bean
    public ValidatorFactory validatorFactory() {
        return Validation.byProvider(HibernateValidator.class)
                .configure()
                .failFast(false)
                .buildValidatorFactory();
    }

    @Bean
    public Validator validator(ValidatorFactory validatorFactory) {
        return validatorFactory.getValidator();
    }
}
