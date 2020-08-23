package com.kd.novel.backend.resipority;

import com.kd.novel.backend.entity.UserLogin;
import com.kd.novel.backend.entity.UserSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface UserSettingsResipority extends CrudRepository<UserSettings,Long>,JpaRepository<UserSettings,Long> {

    Optional<UserSettings> findByUserLogin(UserLogin userLogin);
}
