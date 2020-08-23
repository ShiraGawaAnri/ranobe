package com.kd.novel.backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kd.novel.backend.dto.*;
import com.kd.novel.backend.entity.UserLogin;
import com.kd.novel.backend.vo.JsonResult;

import javax.servlet.http.HttpServletRequest;

public interface UserService {

    JsonResult reg(UserRegRequest entity) throws Exception;

    JsonResult regSimple(UserRegSimpleRequest entity);

    JsonResult login(UserLoginRequest entity) throws Exception;

    JsonResult logout();

    JsonResult getUserInfo();

    JsonResult getUserDetails();

    JsonResult updateUserInfo(UserUpdateRequest entity) throws JsonProcessingException;

    JsonResult changeUserPasswordWithVerification(UserChangePwdRequest entity);

    JsonResult addOrUpdateUserSettings(UserSettingsAddOrUpdateRequest entity);

    void updateUserInfoToNoSql(UserLogin userLogin);

    JsonResult bindMail(VerificationCodeCheckRequest entity);

    JsonResult bindPhone(VerificationCodeCheckRequest entity);

    JsonResult rebindMail(VerificationCodeRebindCheckRequest entity);


}
