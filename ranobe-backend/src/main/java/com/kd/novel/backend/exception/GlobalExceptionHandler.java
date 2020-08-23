package com.kd.novel.backend.exception;

import com.kd.novel.backend.config.constants.DefaultConstants;
import com.kd.novel.backend.vo.JsonResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalExceptionHandler {

    @Value("${spring.profiles.active}")
    private String activeProfile;

    private static final Logger logger = LoggerFactory.getLogger("GlobalExceptionHandler");

    public GlobalExceptionHandler() {
    }

    @ExceptionHandler({RuntimeException.class})
    @ResponseBody
    public JsonResult doHandleRuntimeException(RuntimeException e) {
        if (e instanceof ServiceException) {
            logger.warn("Service Exception  -- {}", e.getMessage());
            return JsonResult.build(DefaultConstants.ERROR_SERVICE, e.getMessage());
        } else if (e instanceof GoogleRecaptchaException){
            logger.warn("GoogleRecaptchaException  -- {}", e.getMessage());
            return JsonResult.build(DefaultConstants.ERROR_IMGCODE, e.getMessage());
        } else {
            e.printStackTrace();
            logger.error("Fatal Error By Global Exception -- {}", e.getMessage());
            return JsonResult.build(DefaultConstants.ERROR_SYSTEM, activeProfile.equals("dev") ? e.getMessage() : "Fatal Error By Global Exception");
        }
    }
}