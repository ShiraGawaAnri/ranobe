package com.kd.novel.backend.dto;

import com.kd.novel.backend.entity.NovelArtist;
import com.kd.novel.backend.entity.NovelCategories;
import com.kd.novel.backend.entity.NovelCover;
import com.kd.novel.backend.entity.NovelTag;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;

@Data
public class NovelAddOrUpdateRequest {

    private Long id;

    @NotBlank(message = "小说名(译名)不能为空")
    private String translateTitle;

    @NotBlank(message = "小说名(原名)不能为空")
    private String originTitle;

    //@NotBlank(message = "小说简介不能为空")
    private String introduction;

    private Integer views;

    private Integer isDelete;

    private Integer validate;

    private Integer finish;

    private Integer locked;

    @NotNull(message = "小说类别不能为空")
    private Long categoryId;

    @NotNull(message = "小说作者不能为空")
    private Long artistId;

    private Set<Long> tagIds;

    private Set<NovelCover> covers;

}
