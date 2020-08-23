package com.kd.novel.backend.dto.base;

import lombok.Data;

@Data
public class PageDTO {

    private Integer page = 0;

    private Integer size = 10;
}
