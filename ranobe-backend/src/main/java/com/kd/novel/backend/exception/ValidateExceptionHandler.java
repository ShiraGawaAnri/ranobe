package com.kd.novel.backend.exception;

import com.kd.novel.backend.config.constants.DefaultConstants;
import com.kd.novel.backend.vo.JsonResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;


@ControllerAdvice
public class ValidateExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public JsonResult ValidateFailedExcption(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        return JsonResult.build(DefaultConstants.ERROR_SERVICE, bindingResult.getFieldError().getDefaultMessage());
    }
}
