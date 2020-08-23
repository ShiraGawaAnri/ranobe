package com.kd.novel.backend.resipority;

import com.kd.novel.backend.dto.NovelCommentGetRequest;
import com.kd.novel.backend.dto.NovelGetRequest;
import com.kd.novel.backend.entity.Novel;
import com.kd.novel.backend.entity.NovelComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

@Service
public interface NovelCommentResipority extends CrudRepository<NovelComment,Long>,JpaRepository<NovelComment,Long>,JpaSpecificationExecutor {

    long countByNovel(Novel novel);
}
