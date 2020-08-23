package com.kd.novel.backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kd.novel.backend.entity.Novel;
import com.kd.novel.backend.entity.NovelHistory;
import lombok.Data;

@Data
public class NovelHistoryDTO extends NovelHistory {

    @JsonIgnore
    private Novel novel;
}
