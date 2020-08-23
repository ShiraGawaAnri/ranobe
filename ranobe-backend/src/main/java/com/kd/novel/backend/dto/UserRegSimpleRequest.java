package com.kd.novel.backend.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class UserRegSimpleRequest {


    @NotBlank(message = "昵称不能为空")
    @Size(min = 2,max = 12,message = "昵称在2-12位之间")
    @Pattern(regexp = "^(?!_)(?!.*?_$)[a-zA-Z0-9_\\u4e00-\\u9fa5]+$", message = "昵称只可含有'中英文','数字'以及,'_'(下划线),不能以下划线开头或结尾")
    private String nickname;

    //@NotBlank(message = "必须先进行人机验证,如验证失败,请刷新网页重试")
    private String recaptchaToken;
}
