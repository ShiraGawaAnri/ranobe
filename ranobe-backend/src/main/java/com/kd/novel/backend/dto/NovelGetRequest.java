package com.kd.novel.backend.dto;

import com.kd.novel.backend.dto.base.PageDTO;
import lombok.Data;

import java.util.List;

@Data
public class NovelGetRequest extends PageDTO {

    private Long id;

    private List<Long> novelIds;

    private String title;

    private String artistName;

    private Long artistId;

    private Long categoryId;

    private List<Long> tagIds;

    private Integer isDelete;

    private Integer validate;

    private Integer finish;

    private Integer sortBy = 0;

    private Long startTime = 0L;

    private Long endTime = 0L;
}
