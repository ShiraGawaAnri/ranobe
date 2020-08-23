package com.kd.novel.backend.resipority;

import com.kd.novel.backend.entity.NovelTags;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

@Service
public interface NovelTagsResipority extends CrudRepository<NovelTags,Long>,JpaRepository<NovelTags,Long> {
}
