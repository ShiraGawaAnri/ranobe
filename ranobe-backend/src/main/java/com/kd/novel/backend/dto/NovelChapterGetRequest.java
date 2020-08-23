package com.kd.novel.backend.dto;


import com.kd.novel.backend.dto.base.PageDTO;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class NovelChapterGetRequest extends PageDTO {

    private Long id;

    @NotNull(message = "小说Id不能为空")
    private Long novelId;

    @NotNull(message = "小说Episode不能为空")
    private Long episode;

    //@NotNull(message = "小说Chapter不能为空")
    private Long chapter;
}
