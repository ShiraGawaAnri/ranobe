package com.kd.novel.backend.config.enums;

public enum PlusHotScoreTimeLimitEnum {

    DEFAULT("default",600L),
    NOVEL("novel",3600L),
    CHAPTER("chapter",60L),
    SUBSCRIBE("subscribe",86400L),
    COMMENT("comment",600L);

    private String name;
    private Long second;


    PlusHotScoreTimeLimitEnum(String name, Long second) {
        this.name = name;
        this.second = second;

    }

    public String getName() {
        return name;
    }

    public Long getSecond() {
        return second;
    }
}
