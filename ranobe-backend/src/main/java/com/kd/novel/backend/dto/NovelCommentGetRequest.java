package com.kd.novel.backend.dto;

import com.kd.novel.backend.dto.base.PageDTO;
import lombok.Data;

import javax.validation.constraints.NotNull;


@Data
public class NovelCommentGetRequest extends PageDTO {

    @NotNull(message = "小说Id不能为空")
    private Long novelId;

    private Integer sortBy = 1;
}
