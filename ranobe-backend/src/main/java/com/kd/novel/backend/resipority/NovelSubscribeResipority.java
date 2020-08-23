package com.kd.novel.backend.resipority;


import com.kd.novel.backend.entity.Novel;
import com.kd.novel.backend.entity.NovelSubscribe;
import com.kd.novel.backend.entity.UserLogin;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public interface NovelSubscribeResipority extends CrudRepository<NovelSubscribe,Long>,JpaRepository<NovelSubscribe,Long> {

    Page<NovelSubscribe> findByUserLogin(Pageable pageable, UserLogin userLogin);

    Optional<NovelSubscribe> findByNovelAndUserLogin(Novel novel, UserLogin userLogin);

    long countByNovel(Novel novel);

    @Query("select ns.userLogin.username from NovelSubscribe ns where ns.novel.id = :#{#novel.id} and ns.hasNew = 0")
    List<String> getNotifyNovelHasNewUserIds(@Param("novel")Novel novel);

    @Transactional
    @Modifying
    @Query("update NovelSubscribe ns set ns.hasNew = 1 where ns.novel.id = :#{#novel.id} and ns.hasNew = 0")
    Integer notifyNovelHasNew(@Param("novel")Novel novel);
}
