package com.kd.novel.backend.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class NovelChapterParagraphCommentGetRequest {

    @NotEmpty(message = "段落ID不能为空")
    private List<Long> paragraphIds;
}
