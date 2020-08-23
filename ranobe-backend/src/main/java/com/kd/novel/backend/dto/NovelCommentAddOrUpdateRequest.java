package com.kd.novel.backend.dto;


import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

@Data
public class NovelCommentAddOrUpdateRequest {

    @NotNull(message = "小说Id不能为空")
    private Long novelId;

    private Long parentId;

    @NotNull(message = "评论内容不能为空")
    @Length(min = 5,max = 1000,message = "评论内容只能在5-1000字内")
    private String mes;
}
