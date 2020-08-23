package com.kd.novel.backend.resipority;

import com.kd.novel.backend.entity.Novel;
import com.kd.novel.backend.entity.NovelLikes;
import com.kd.novel.backend.entity.UserLogin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface NovelLikesResipority extends CrudRepository<NovelLikes,Long>,JpaRepository<NovelLikes,Long> {

    Optional<NovelLikes> findByNovelAndUserLogin(Novel novel, UserLogin userLogin);

    long countByNovel(Novel novel);
}
