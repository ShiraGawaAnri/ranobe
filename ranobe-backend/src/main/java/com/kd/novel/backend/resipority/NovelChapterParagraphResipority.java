package com.kd.novel.backend.resipority;


import com.kd.novel.backend.entity.NovelChapter;
import com.kd.novel.backend.entity.NovelChapterParagraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface NovelChapterParagraphResipority extends CrudRepository<NovelChapterParagraph,Long>,JpaRepository<NovelChapterParagraph,Long> {

    List<NovelChapterParagraph> findByNovelChapter(NovelChapter novelChapter);

    void deleteByNovelChapterEqualsAndNumGreaterThan(NovelChapter novelChapter,Integer num);
}
