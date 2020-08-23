package com.kd.novel.backend.dto;

import com.kd.novel.backend.entity.Novel;
import lombok.Data;

@Data
public class RankNovelDTO {

    private Novel novel;

    private Double score;
}
