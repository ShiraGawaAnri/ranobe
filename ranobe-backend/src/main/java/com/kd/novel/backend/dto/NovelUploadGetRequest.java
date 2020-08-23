package com.kd.novel.backend.dto;

import com.kd.novel.backend.dto.base.PageDTO;
import lombok.Data;

@Data
public class NovelUploadGetRequest extends PageDTO {

    private Integer sortBy = 1;
}
