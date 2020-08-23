package com.kd.novel.backend.controller;

import com.kd.novel.backend.config.annotation.UserAccess;
import com.kd.novel.backend.config.constants.DefaultConstants;
import com.kd.novel.backend.dto.VerificationCodeCheckByTypeRequest;
import com.kd.novel.backend.dto.VerificationCodeCheckRequest;
import com.kd.novel.backend.dto.VerificationCodeSendByTypeRequest;
import com.kd.novel.backend.dto.VerificationCodeSendRequest;
import com.kd.novel.backend.exception.ServiceException;
import com.kd.novel.backend.service.VerificationCodeService;
import com.kd.novel.backend.vo.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/verification")
public class VerificationCodeController {

    @Autowired
    private VerificationCodeService verificationCodeService;

    @GetMapping("/check")
    public JsonResult vCodeCheck(@Validated VerificationCodeCheckRequest entity){
        try {
            return verificationCodeService.vCodeCheck(entity);
        } catch (ServiceException e) {
            return JsonResult.build(DefaultConstants.ERROR_SERVICE, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.build(DefaultConstants.ERROR_SYSTEM, "VerificationCode Check Error");
        }
    }

    @GetMapping("/check/type")
    @UserAccess
    public JsonResult vCodeCheckByType(@Validated VerificationCodeCheckByTypeRequest entity){
        try {
            return verificationCodeService.vCodeCheckByType(entity);
        } catch (ServiceException e) {
            return JsonResult.build(DefaultConstants.ERROR_SERVICE, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.build(DefaultConstants.ERROR_SYSTEM, "VerificationCode Check By Type Error");
        }
    }


    @PostMapping()
    @UserAccess
    public JsonResult vCodeSendByType(@RequestBody @Validated VerificationCodeSendByTypeRequest entity){
        try {
            return verificationCodeService.vCodeSendByType(entity);
        } catch (ServiceException e) {
            return JsonResult.build(DefaultConstants.ERROR_SERVICE, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.build(DefaultConstants.ERROR_SYSTEM, "VerificationCode Send By Type Error");
        }
    }

    @PostMapping("/phone")
    public JsonResult vCodeSendToPhoneRequest(@RequestBody @Validated VerificationCodeSendRequest entity){
        try {
            return verificationCodeService.vCodeSendToPhoneRequest(entity);
        } catch (ServiceException e) {
            return JsonResult.build(DefaultConstants.ERROR_SERVICE, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.build(DefaultConstants.ERROR_SYSTEM, "VerificationCode Send To Phone Error");
        }
    }

    @PostMapping("/email")
    public JsonResult vCodeSendToEmailRequest(@RequestBody @Validated VerificationCodeSendRequest entity){
        try {
            return verificationCodeService.vCodeSendToEmailRequest(entity);
        } catch (ServiceException e) {
            return JsonResult.build(DefaultConstants.ERROR_SERVICE, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.build(DefaultConstants.ERROR_SYSTEM, "VerificationCode Send To Email Error");
        }
    }
}
