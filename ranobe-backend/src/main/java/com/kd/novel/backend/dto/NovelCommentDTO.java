package com.kd.novel.backend.dto;

import com.kd.novel.backend.entity.NovelComment;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class NovelCommentDTO extends NovelComment {

    private Set<NovelComment> quoteComment;
}
