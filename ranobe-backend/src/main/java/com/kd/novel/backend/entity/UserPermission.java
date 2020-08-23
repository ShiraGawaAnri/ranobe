package com.kd.novel.backend.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "user_permission")
@Getter
@Setter
@DynamicUpdate
public class UserPermission {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String description;

    private String role;

    @OneToMany(mappedBy="userPermission")
    @JsonBackReference(value = "userRolePermissions")
    private Set<UserRolePermission> userRolePermissions = new HashSet<>();

}
