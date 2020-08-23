package com.kd.novel.backend.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

@Data
public class NovelChapterParagraphCommentAddOrUpdateRequest {

    @NotNull(message = "段落Id不能为空")
    private Long paragraphId;

    private Long parentId;

    @NotNull(message = "吐槽内容不能为空")
    @Length(min = 5,max = 30,message = "吐槽内容只能在5-100字内")
    private String mes;
}
