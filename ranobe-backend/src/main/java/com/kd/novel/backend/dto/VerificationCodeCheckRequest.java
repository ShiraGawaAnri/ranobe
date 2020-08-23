package com.kd.novel.backend.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class VerificationCodeCheckRequest {

    @NotBlank(message = "验证码不能为空")
    private String vcode;

    //Email or phone
    @NotBlank(message = "请求地址不能为空")
    private String code;
}
