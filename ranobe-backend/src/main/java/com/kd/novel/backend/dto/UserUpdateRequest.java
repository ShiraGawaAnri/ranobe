package com.kd.novel.backend.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class UserUpdateRequest {

    @NotBlank(message = "昵称不能为空")
    @Size(min = 2,max = 12,message = "昵称在2-12位之间")
    @Pattern(regexp = "^(?!_)(?!.*?_$)[a-zA-Z0-9_\\u4e00-\\u9fa5]+$", message = "昵称只可含有'中英文','数字'以及,'_'(下划线),不能以下划线开头或结尾")
    private String nickname;

    @Pattern(regexp = "(^$)|^(https?)://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]",message = "连接非法")
    private String avatar;
}
