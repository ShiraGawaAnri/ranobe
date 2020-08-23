package com.kd.novel.backend.resipority;


import com.kd.novel.backend.entity.Novel;
import com.kd.novel.backend.entity.NovelChapter;
import com.kd.novel.backend.entity.NovelHistory;
import com.kd.novel.backend.entity.UserLogin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface NovelHistoryResipority extends CrudRepository<NovelHistory,Long>,JpaRepository<NovelHistory,Long> {

    Page<NovelHistory> findByUserLogin(Pageable pageable,UserLogin userLogin);

    List<NovelHistory> findByUserLoginOrderByLastReadTimeDesc(UserLogin userLogin);

    Optional<NovelHistory> findByNovelChapterAndUserLogin(NovelChapter novelChapter, UserLogin userLogin);

    Optional<NovelHistory> findByNovelAndUserLogin(Novel novel, UserLogin userLogin);
}
