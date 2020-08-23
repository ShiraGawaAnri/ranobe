package com.kd.novel.backend.dto;

import com.kd.novel.backend.entity.NovelCover;
import lombok.Data;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import java.util.Set;


@Data
public class NovelChapterAddOrUpdateRequest {

    private Long id;

    @NotNull(message = "小说本/卷Id不能为空")
    private Long episodeId;

    @NotNull(message = "序数不能为空")
    private Long chapter;

    @NotBlank(message = "标题(译名)不能为空")
    private String translateTitle;

    private String originTitle;

    private String translateContent;

    private String originContent;

    private String headerInfo;

    private String footerInfo;

    private Integer allContentChange = 0;

    private Integer deleteAllParagraphComment = 0;

    private Map<Integer,String> originParagraphs;

    private Map<Integer,String> translateParagraphs;
}
