package com.kd.novel.backend.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class NovelLikesAddOrUpdateRequest {

    @NotNull(message = "小说Id不能为空")
    private Long novelId;

    @NotNull(message = "like值不能为空")
    private Integer value;
}
