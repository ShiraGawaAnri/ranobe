package com.kd.novel.backend.dto;


import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class UserLoginRequest {

    @NotBlank(message = "账号名不能为空")
    @Size(min = 4,max = 16,message = "账号名在4-16位之间")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "账号名不可含有除数字以及英文外的非法字符")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 4,max = 16,message = "密码在4-16位之间")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "密码不可含有除数字以及英文外的非法字符")
    private String password;

    //@NotBlank(message = "必须先进行人机验证,如验证失败,请刷新网页重试")
    private String recaptchaToken;
}
