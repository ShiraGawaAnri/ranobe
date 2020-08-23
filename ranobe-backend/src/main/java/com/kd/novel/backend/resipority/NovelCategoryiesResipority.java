package com.kd.novel.backend.resipority;

import com.kd.novel.backend.entity.NovelCategories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

@Service
public interface NovelCategoryiesResipority extends CrudRepository<NovelCategories,Long>,JpaRepository<NovelCategories,Long> {
}
