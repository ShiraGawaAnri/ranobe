package com.kd.novel.backend.resipority;

import com.kd.novel.backend.entity.Novel;
import com.kd.novel.backend.entity.UserLogin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface NovelResipority extends CrudRepository<Novel,Long>,JpaRepository<Novel,Long>,JpaSpecificationExecutor {

    Page findAll(Specification spec, Pageable pageable);

    Optional<Novel> findById(Long id);

    List<Novel> findByTranslateTitleOrOriginTitle(String translateTitle, String originTitle);

    Page findByUserLogin(Pageable pageable, UserLogin userLogin);

    List<Novel> findByIdIn(List<Long> ids);
}
