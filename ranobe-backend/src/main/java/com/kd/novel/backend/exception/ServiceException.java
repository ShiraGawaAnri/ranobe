package com.kd.novel.backend.exception;

import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Service;

import java.io.Serializable;

@Aspect
@Service
public class ServiceException extends RuntimeException implements Serializable {
    private static final long serialVersionUID = 1L;


    public ServiceException() {
    }

    public ServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }


    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(Throwable cause) {
        super(cause);
    }
}
