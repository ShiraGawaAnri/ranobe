package com.kd.novel.backend.resipority;

import com.kd.novel.backend.entity.Novel;
import com.kd.novel.backend.entity.NovelCover;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface NovelCoverResipority extends CrudRepository<NovelCover,Long>,JpaRepository<NovelCover,Long> {

    void deleteAllByNovel(Novel novel);
}
