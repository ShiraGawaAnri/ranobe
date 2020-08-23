package com.kd.novel.backend.dto;


import com.kd.novel.backend.dto.base.PageDTO;
import lombok.Data;

@Data
public class HotNovelGetRequest extends PageDTO {

    private Long startTime;

    private Long endTime;
}
