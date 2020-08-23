package com.kd.novel.backend.entity;


import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.kd.novel.backend.entity.base.BaseEntity;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "user_login")
@Getter
@Setter
@DynamicUpdate
//@JsonIdentityInfo(generator=ObjectIdGenerators.IntSequenceGenerator.class, property="@id")
public class UserLogin {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id = 0L;

    @Column(nullable = false,unique = true)
    @JsonIgnore
    private String username;

    @Column(nullable = false)
    @JsonIgnore
    private String password;

    @Column(nullable = false,unique = true)
    private String nickname;

    @Column(nullable = false,unique = true)
    @JsonIgnore
    private String email;


    @JsonIgnore
    private String phone;

    @Column(nullable = false)
    private Integer validate;

    private String avatar;

    @JsonIgnore
    private String salt;

    private Integer zone;

    private Integer status;

    private Integer groupId;

    private Long groupExpired;

    private Integer rebindChance = 0;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @CreationTimestamp
    @JsonIgnore
    private Date createdTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @UpdateTimestamp
    @JsonIgnore
    private Date updatedTime;

    @OneToMany(cascade = CascadeType.REMOVE,mappedBy="userLogin")
    @JsonManagedReference(value = "loginRole")
    private Set<UserLoginRole> loginRole = new HashSet<>();

    @OneToOne(cascade = CascadeType.REMOVE,mappedBy="userLogin")
    private UserSettings userSettings;

    @OneToMany(cascade = CascadeType.REMOVE,mappedBy="userLogin")
    @JsonManagedReference(value = "novels")
    //@EqualsAndHashCode.Exclude @ToString.Exclude
    private Set<Novel> novels = new HashSet<>();

    @OneToMany(cascade = CascadeType.REMOVE,mappedBy="userLogin")
    //@JsonManagedReference(value = "user-novelEpisodes")
    //@EqualsAndHashCode.Exclude @ToString.Exclude
    @JsonIgnore
    private Set<NovelEpisode> novelEpisodes = new HashSet<>();

    @OneToMany(cascade = CascadeType.REMOVE,mappedBy="userLogin")
    //@JsonManagedReference(value = "user-novelSubscribes")
    //@EqualsAndHashCode.Exclude @ToString.Exclude
    private Set<NovelSubscribe> novelSubscribes = new HashSet<>();

    @OneToMany(cascade = CascadeType.REMOVE,mappedBy="userLogin")
    //@JsonManagedReference(value = "user-novelComments")
    //@EqualsAndHashCode.Exclude @ToString.Exclude
    private Set<NovelComment> novelComments = new HashSet<>();

    @OneToMany(cascade = CascadeType.REMOVE,mappedBy="userLogin")
    @JsonManagedReference(value = "user-novelChapterComments")
    //@EqualsAndHashCode.Exclude @ToString.Exclude
    private Set<NovelChapterComment> novelChapterComments = new HashSet<>();

    @OneToMany(cascade = CascadeType.REMOVE,mappedBy="userLogin")
    //@JsonManagedReference(value = "user-novelChapterParagraphComments")
    //@EqualsAndHashCode.Exclude @ToString.Exclude
    private Set<NovelChapterParagraphComment> novelChapterParagraphComments = new HashSet<>();

    @OneToMany(cascade = CascadeType.REMOVE,mappedBy="userLogin")
    @JsonManagedReference(value = "user-novelHistories")
    //@EqualsAndHashCode.Exclude @ToString.Exclude
    private Set<NovelHistory> novelHistories = new HashSet<>();

    @OneToMany(cascade = CascadeType.REMOVE,mappedBy="userLogin")
    @JsonManagedReference(value = "user-novelLikes")
    //@EqualsAndHashCode.Exclude @ToString.Exclude
    private Set<NovelLikes> novelLikes = new HashSet<>();

    @OneToMany(cascade = CascadeType.REMOVE,mappedBy="userLogin")
    @JsonManagedReference(value = "user-novelCommentLikes")
    private Set<NovelCommentLikes> novelCommentLikes = new HashSet<>();

    @OneToMany(cascade = CascadeType.REMOVE,mappedBy="userLogin")
    @JsonManagedReference(value = "user-novelCommentDislikes")
    //@EqualsAndHashCode.Exclude @ToString.Exclude
    private Set<NovelCommentDislikes> novelCommentDislikes = new HashSet<>();

    @OneToMany(cascade = CascadeType.REMOVE,mappedBy="userLogin")
    @JsonIgnore
    private Set<FeedbackHistory> userFeedbackHistories = new HashSet<>();

//    @OneToMany(cascade = CascadeType.ALL,mappedBy="userLogin")
//    //@JsonManagedReference(value = "feedbackHistories")
//    //@EqualsAndHashCode.Exclude @ToString.Exclude
//    @JsonIgnore
//    private Set<FeedbackHistory> operatorFeedbackHistories = new HashSet<>();

    @Version
    private Long version = 0L;

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof UserLogin)) {
            return false;
        }

        return id != null && id.equals(((UserLogin) obj).id);
    }

    @Override
    public int hashCode() {
        return 2018;
    }


}

