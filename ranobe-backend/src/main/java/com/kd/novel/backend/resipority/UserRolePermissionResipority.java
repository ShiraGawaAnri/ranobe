package com.kd.novel.backend.resipority;

import com.kd.novel.backend.entity.UserRolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public interface UserRolePermissionResipority extends JpaRepository<UserRolePermission,Long> {
}
