package com.kd.novel.backend.dto;

import lombok.Data;

@Data
public class UserSettingsAddOrUpdateRequest {

    private Integer showTags = 1;

    private Integer fontSize = 24;

    private Integer showTsukomi = 1;

    private Integer language = 1;

    private Integer showOriginContent = 0;

    private Integer fontColorNumber = 1;

    private Integer backgroundColorNumber = 1;

    private Integer closeSiderBar = 0;
}
