package com.kd.novel.backend.resipority;

import com.kd.novel.backend.entity.UserLoginRole;
import com.kd.novel.backend.entity.UserLogin;
import com.kd.novel.backend.entity.UserLoginRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public interface UserLoginRoleResipority extends JpaRepository<UserLoginRole,Long> {
}
