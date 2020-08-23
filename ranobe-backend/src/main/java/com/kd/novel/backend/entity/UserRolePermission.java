package com.kd.novel.backend.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "user_role_permission")
@Getter
@Setter
@DynamicUpdate
public class UserRolePermission {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name="roleId",referencedColumnName = "id")
    @JsonBackReference(value = "userRolePermissions")
    private UserRole userRole;

    @ManyToOne
    @JoinColumn(name="permissionId",referencedColumnName = "id")
    private UserPermission userPermission;

    @Override
    public int hashCode() {
        return 2018;
    }
}
