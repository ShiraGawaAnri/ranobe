package com.kd.novel.backend.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class VerificationCodeRebindCheckRequest {

    @NotBlank(message = "验证码不能为空")
    private String vcode;

    @NotBlank(message = "验证码不能为空")
    private String vconfrimcode;

    //Email or phone
    @NotBlank(message = "请求地址不能为空")
    private String code;

}
