package com.kd.novel.backend.service;

import com.kd.novel.backend.dto.VerificationCodeCheckByTypeRequest;
import com.kd.novel.backend.dto.VerificationCodeCheckRequest;
import com.kd.novel.backend.dto.VerificationCodeSendByTypeRequest;
import com.kd.novel.backend.dto.VerificationCodeSendRequest;
import com.kd.novel.backend.vo.JsonResult;

public interface VerificationCodeService {

    JsonResult vCodeCheck(VerificationCodeCheckRequest entity);

    JsonResult vCodeCheckByType(VerificationCodeCheckByTypeRequest entity);

    JsonResult vCodeSendByType(VerificationCodeSendByTypeRequest entity);

    JsonResult vCodeSendToPhoneRequest(VerificationCodeSendRequest entity);

    JsonResult vCodeSendToEmailRequest(VerificationCodeSendRequest entity);

}
