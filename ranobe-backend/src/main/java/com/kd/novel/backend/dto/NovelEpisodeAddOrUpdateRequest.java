package com.kd.novel.backend.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class NovelEpisodeAddOrUpdateRequest {

    private Long id;

    @NotNull(message = "小说Id不能为空")
    private Long novelId;

    @NotNull(message = "卷/本数不能为空")
    private Long episode;

    @NotBlank(message = "小说卷/本标题不能为空")
    private String translateTitle;

    @NotBlank(message = "小说卷/本标题不能为空")
    private String originTitle;

    private Integer validate;

    private Integer views;
}
