package com.kd.novel.backend.resipority;

import com.kd.novel.backend.entity.NovelComment;
import com.kd.novel.backend.entity.NovelCommentDislikes;
import com.kd.novel.backend.entity.NovelCommentLikes;
import com.kd.novel.backend.entity.UserLogin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface NovelCommentDislikesResipority extends CrudRepository<NovelCommentDislikes,Long>,JpaRepository<NovelCommentDislikes,Long> {

    Optional<NovelCommentDislikes> findByNovelCommentAndUserLogin(NovelComment novelComment, UserLogin userLogin);

    long countByNovelComment(NovelComment novelComment);
}