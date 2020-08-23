package com.kd.novel.backend.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class NovelCommentDislikesAddOrUpdateRequest {

    @NotNull(message = "评论Id不能为空")
    private Long id;

    @NotNull(message = "like值不能为空")
    private Integer value;
}
