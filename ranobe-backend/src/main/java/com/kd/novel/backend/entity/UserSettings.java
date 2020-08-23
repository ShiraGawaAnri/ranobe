package com.kd.novel.backend.entity;

import com.fasterxml.jackson.annotation.*;
import com.kd.novel.backend.entity.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "user_settings")
@Getter
@Setter
@DynamicUpdate
//@JsonIdentityInfo(generator=ObjectIdGenerators.IntSequenceGenerator.class, property="@id")
public class UserSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne
    @JoinColumn(name="userId",referencedColumnName = "id")
    @JsonIgnore
    private UserLogin userLogin;

    private Integer showTags = 1;

    private Integer fontSize = 24;

    private Integer showTsukomi = 1;

    private Integer language = 1;

    private Integer showOriginContent = 0;

    private Integer fontColorNumber = 1;

    private Integer backgroundColorNumber = 1;

    private Integer closeSiderBar = 0;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @UpdateTimestamp
    private Date updatedTime;

    @Version
    @Column(columnDefinition = "bigint DEFAULT 0")
    private Long version;

}
