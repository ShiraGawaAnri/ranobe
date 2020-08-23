package com.kd.novel.backend.dto;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kd.novel.backend.entity.NovelEpisode;
import com.kd.novel.backend.entity.UserLogin;
import lombok.Data;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Data
public class NovelHistoryAddOrUpdateRequest {

    @NotNull(message = "小说Id不能为空")
    private Long novelId;

    private Long chapter;

    private Long episode;

    private Long readParagraph;

    private Long readWordCount;

    private Long readPage;
}
