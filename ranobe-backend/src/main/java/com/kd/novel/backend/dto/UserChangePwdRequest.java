package com.kd.novel.backend.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class UserChangePwdRequest {

    @NotBlank(message = "验证码不能为空")
    private String vcode;

    @NotBlank(message = "密码不能为空")
    @Size(min = 4,max = 16,message = "密码在4-16位之间")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "密码不可含有除数字以及英文外的非法字符")
    private String password;
}
