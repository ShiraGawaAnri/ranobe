package com.kd.novel.backend.config.enums;

public enum RoleEnum {

    ANY("游客","ROLE_ANONYMOUS"),
    USER("普通用户", "ROLE_USER"),
    BRONZON("青铜用户","ROLE_BROZON_USER"),
    SILVER("白银用户", "ROLE_SILVER_USER"),
    GOLDEN("绯绯金用户", "ROLE_GOLDEN_USER"),
    ADMIN("管理员", "ROLE_ADMIN"),
    SUPERADMIN("超级管理员", "ROLE_ROOT");

    private String desc;
    private String name;

    RoleEnum(String desc, String name) {
        this.desc = desc;
        this.name = name;

    }

    public String getDesc() {
        return this.desc;
    }

    public String getName() {
        return name;
    }
}
