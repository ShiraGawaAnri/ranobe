package com.kd.novel.backend.config.enums;

public enum RankEnum {

    NovelView("热门小说榜","HOTNOVEL");

    private String desc;
    private String name;

    RankEnum(String desc, String name) {
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
