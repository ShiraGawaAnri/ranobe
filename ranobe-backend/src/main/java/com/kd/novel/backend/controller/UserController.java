package com.kd.novel.backend.controller;

import com.kd.novel.backend.dto.*;
import com.kd.novel.backend.exception.ServiceException;
import com.kd.novel.backend.config.annotation.UserAccess;
import com.kd.novel.backend.config.constants.DefaultConstants;
import com.kd.novel.backend.service.UserService;
import com.kd.novel.backend.vo.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/user/info")
    @UserAccess
    public JsonResult getUserInfo(){
        try {
            return userService.getUserInfo();
        } catch (ServiceException e) {
            return JsonResult.build(DefaultConstants.ERROR_SERVICE, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.build(DefaultConstants.ERROR_SYSTEM, "Get User Info Error");
        }
    }

    @GetMapping("/user/details")
    @UserAccess
    public JsonResult getUserDetails(){
        try {
            return userService.getUserDetails();
        } catch (ServiceException e) {
            return JsonResult.build(DefaultConstants.ERROR_SERVICE, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.build(DefaultConstants.ERROR_SYSTEM, "Get User Details Error");
        }
    }

    @PutMapping("/user")
    @UserAccess
    public JsonResult updateUserInfo(@Validated @RequestBody UserUpdateRequest entity){
        try {
            return userService.updateUserInfo(entity);
        } catch (ServiceException e) {
            return JsonResult.build(DefaultConstants.ERROR_SERVICE, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.build(DefaultConstants.ERROR_SYSTEM, "Update User Info Error");
        }
    }

    @PutMapping("/user/password")
    @UserAccess
    public JsonResult changeUserPasswordWithVerification(@Validated @RequestBody UserChangePwdRequest entity){
        try {
            return userService.changeUserPasswordWithVerification(entity);
        } catch (ServiceException e) {
            return JsonResult.build(DefaultConstants.ERROR_SERVICE, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.build(DefaultConstants.ERROR_SYSTEM, "Update User Info With Verification Error");
        }
    }

    @PutMapping("/user/mail")
    @UserAccess
    public JsonResult bindMail(@Validated @RequestBody VerificationCodeCheckRequest entity){
        try {
            return userService.bindMail(entity);
        } catch (ServiceException e) {
            return JsonResult.build(DefaultConstants.ERROR_SERVICE, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.build(DefaultConstants.ERROR_SYSTEM, "Bind Mail Error");
        }
    }

    @PutMapping("/user/rebind/mail")
    @UserAccess
    public JsonResult rebindMail(@Validated @RequestBody VerificationCodeRebindCheckRequest entity){
        try {
            return userService.rebindMail(entity);
        } catch (ServiceException e) {
            return JsonResult.build(DefaultConstants.ERROR_SERVICE, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.build(DefaultConstants.ERROR_SYSTEM, "Bind Mail Error");
        }
    }

    @PutMapping("/user/phone")
    @UserAccess
    public JsonResult bindPhone(@Validated @RequestBody VerificationCodeCheckRequest entity){
        try {
            return userService.bindPhone(entity);
        } catch (ServiceException e) {
            return JsonResult.build(DefaultConstants.ERROR_SERVICE, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.build(DefaultConstants.ERROR_SYSTEM, "Bind Phone Error");
        }
    }

    @PutMapping("/user/settings")
    @UserAccess
    public JsonResult addOrUpdateUserSettings(@Validated @RequestBody UserSettingsAddOrUpdateRequest entity){
        try {
            return userService.addOrUpdateUserSettings(entity);
        } catch (ServiceException e) {
            return JsonResult.build(DefaultConstants.ERROR_SERVICE, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.build(DefaultConstants.ERROR_SYSTEM, "AddOrUpdate UserSettings Info Error");
        }
    }

    @PostMapping("/auth/reg")
    public JsonResult reg(@Validated @RequestBody UserRegRequest entity){
        try {
            return userService.reg(entity);
        } catch (ServiceException e) {
            return JsonResult.build(DefaultConstants.ERROR_SERVICE, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.build(DefaultConstants.ERROR_SYSTEM, "Reg Error");
        }
    }

    @PostMapping("/auth/reg/simple")
    public JsonResult regSimple(@Validated @RequestBody UserRegSimpleRequest entity){
        try {
            return userService.regSimple(entity);
        } catch (ServiceException e) {
            return JsonResult.build(DefaultConstants.ERROR_SERVICE, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.build(DefaultConstants.ERROR_SYSTEM, "Reg Simple Error");
        }
    }

    @PostMapping("/login")
    public JsonResult reg(@Validated @RequestBody UserLoginRequest entity){
        try {
            return userService.login(entity);
        } catch (ServiceException e) {
            return JsonResult.build(DefaultConstants.ERROR_SERVICE, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.build(DefaultConstants.ERROR_SYSTEM, "Login Error");
        }
    }

    @RequestMapping("/auth/logout")
    public JsonResult reg(){
        try {
            return userService.logout();
        } catch (ServiceException e) {
            return JsonResult.build(DefaultConstants.ERROR_SERVICE, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.build(DefaultConstants.ERROR_SYSTEM, "Logout Error");
        }
    }
}
