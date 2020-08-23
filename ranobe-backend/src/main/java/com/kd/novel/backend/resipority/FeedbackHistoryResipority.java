package com.kd.novel.backend.resipority;

import com.kd.novel.backend.entity.FeedbackHistory;
import com.kd.novel.backend.entity.UserLogin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface FeedbackHistoryResipority extends CrudRepository<FeedbackHistory,Long>,JpaRepository<FeedbackHistory,Long> {

    List<FeedbackHistory> findByUserLogin(UserLogin userLogin);

    Page<FeedbackHistory> findByUserLogin(Pageable pageable, UserLogin userLogin);

    long countByUserLoginAndStatus(UserLogin userLogin, Integer status);
}
