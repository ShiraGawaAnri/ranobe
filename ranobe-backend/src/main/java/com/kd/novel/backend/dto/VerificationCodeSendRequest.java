package com.kd.novel.backend.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class VerificationCodeSendRequest {

    //邮箱or手机号
    @NotNull(message = "请求地址不能为空")
    private String code;
}
