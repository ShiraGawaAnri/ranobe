package com.kd.novel.backend.config.jwt;

import lombok.Data;

import java.util.List;

@Data
public class MyJwtUserDetails {

    private String username;

    private String password;

    private List<String> roles;
}
