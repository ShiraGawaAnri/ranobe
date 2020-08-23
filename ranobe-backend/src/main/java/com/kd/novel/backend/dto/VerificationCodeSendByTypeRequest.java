package com.kd.novel.backend.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class VerificationCodeSendByTypeRequest {

    @NotNull(message = "发送地址的类型不能为空")
    private Integer type;
}
