package com.kd.novel.backend.resipority;

import com.kd.novel.backend.entity.NovelChapterParagraph;
import com.kd.novel.backend.entity.NovelChapterParagraphComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface NovelChapterParagraphCommentResipority extends CrudRepository<NovelChapterParagraphComment,Long>,JpaRepository<NovelChapterParagraphComment,Long> {

    List<NovelChapterParagraphComment> findByIdIn(List<Long> ids);

    List<NovelChapterParagraphComment> findByNovelChapterParagraph(NovelChapterParagraph novelChapterParagraph);
}
