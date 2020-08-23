package com.kd.novel.backend.dto;


import com.kd.novel.backend.dto.base.PageDTO;
import lombok.Data;

@Data
public class NovelSubscribeGetRequest extends PageDTO {

    private Integer sortBy = 0;
}
