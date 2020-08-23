package com.kd.novel.backend.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "user_role")
@Getter
@Setter
@DynamicUpdate
public class UserRole {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String description;


    @OneToMany(mappedBy="userRole",fetch=FetchType.EAGER)
    @JsonBackReference(value = "loginRole")
    private Set<UserLoginRole> loginRole = new HashSet<>();

    @OneToMany(mappedBy="userRole",fetch=FetchType.EAGER)
    @JsonManagedReference(value = "userRolePermissions")
    private Set<UserRolePermission> userRolePermissions = new HashSet<>();

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof UserRole)) {
            return false;
        }

        return id != null && id.equals(((UserRole) obj).id);
    }

    @Override
    public int hashCode() {
        return 2018;
    }

}
