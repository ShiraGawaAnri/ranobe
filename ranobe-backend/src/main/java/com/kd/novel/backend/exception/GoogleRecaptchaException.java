package com.kd.novel.backend.exception;

import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Service;

import java.io.Serializable;

@Aspect
@Service
public class GoogleRecaptchaException extends RuntimeException implements Serializable {
    private static final long serialVersionUID = 1L;


    public GoogleRecaptchaException() {
    }

    public GoogleRecaptchaException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public GoogleRecaptchaException(String message, Throwable cause) {
        super(message, cause);
    }


    public GoogleRecaptchaException(String message) {
        super(message);
    }

    public GoogleRecaptchaException(Throwable cause) {
        super(cause);
    }
}
