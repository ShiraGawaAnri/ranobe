package com.kd.novel.backend.resipority;

import com.kd.novel.backend.entity.Novel;
import com.kd.novel.backend.entity.NovelTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

@Service
public interface NovelTagResipority extends CrudRepository<NovelTag,Long>,JpaRepository<NovelTag,Long>{

    void deleteAllByNovel(Novel novel);
}
