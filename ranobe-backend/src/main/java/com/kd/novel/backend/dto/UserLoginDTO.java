package com.kd.novel.backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.kd.novel.backend.entity.UserLogin;
import com.kd.novel.backend.entity.UserLogin;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class UserLoginDTO extends UserLogin{

    private String uname;

    @JsonIgnore
    private Long version;

    @JsonIgnore
    private String password;

    @JsonIgnore
    private String salt;

    private List<String> roles;

    private String introduction;
}
