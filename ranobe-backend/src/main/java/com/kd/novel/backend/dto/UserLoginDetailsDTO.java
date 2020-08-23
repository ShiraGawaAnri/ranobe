package com.kd.novel.backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class UserLoginDetailsDTO {

    private Long id;

    private String username;

    private String nickname;

    private String email;

    private String phone;

    private Integer validate;

    private String avatar;

    private Integer zone;

    private Integer status;

    private Integer groupId;

    private Long groupExpired;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdTime;

}
