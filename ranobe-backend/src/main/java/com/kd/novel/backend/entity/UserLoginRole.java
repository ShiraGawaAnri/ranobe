package com.kd.novel.backend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Entity
@Table(name = "user_login_role")
@Getter
@Setter
@JsonIgnoreProperties(value={"hibernateLazyInitializer","handler","fieldHandler"})
@DynamicUpdate
public class UserLoginRole {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="userId",referencedColumnName = "id")
    @JsonBackReference(value = "loginRole")
    private UserLogin userLogin;

    @ManyToOne
    @JoinColumn(name="roleId",referencedColumnName = "id")
    //@JsonBackReference
    private UserRole userRole;

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof UserLoginRole)) {
            return false;
        }

        return id != null && id.equals(((UserLoginRole) obj).id);
    }

    @Override
    public int hashCode() {
        return 2018;
    }
}
