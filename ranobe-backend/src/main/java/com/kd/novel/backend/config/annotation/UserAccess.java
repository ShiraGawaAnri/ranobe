package com.kd.novel.backend.config.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface UserAccess {

    /**
     * 访问允许的角色，默认不限制角色
     */
    String[] value() default "";

    /**
     * 访问是否需要登录，默认需要登录
     */
    boolean needLogin() default true;

    String message() default "请登录后再访问";
}
