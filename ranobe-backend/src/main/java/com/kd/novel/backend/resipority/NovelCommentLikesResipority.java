package com.kd.novel.backend.resipority;

import com.kd.novel.backend.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface NovelCommentLikesResipority extends CrudRepository<NovelCommentLikes,Long>,JpaRepository<NovelCommentLikes,Long> {

    Optional<NovelCommentLikes> findByNovelCommentAndUserLogin(NovelComment novelComment, UserLogin userLogin);

    long countByNovelComment(NovelComment novelComment);
}