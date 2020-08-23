package com.kd.novel.backend.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class VerificationCodeCheckByTypeRequest {


    @NotBlank(message = "验证码不能为空")
    private String vcode;

    @NotNull(message = "验证的类型不能为空")
    private Integer type;
}
