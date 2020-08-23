package com.kd.novel.backend.resipority;

import com.kd.novel.backend.entity.UserLogin;
import com.kd.novel.backend.entity.UserLogin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface UserLoginResipority extends JpaRepository<UserLogin,Long> {

    Optional<UserLogin> findById(Long id);

    Optional<UserLogin> findByUsernameEquals(String username);

    Optional<UserLogin> findByNicknameEquals(String nickname);

    Optional<UserLogin> findByEmailEquals(String email);

}
