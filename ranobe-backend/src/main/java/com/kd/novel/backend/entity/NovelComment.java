package com.kd.novel.backend.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Entity
@Table(name = "novel_comment")
@Getter
@Setter
@DynamicUpdate
public class NovelComment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Lob
    @Column(nullable = false,length = 2000)
    private String mes;

    //private Long floor;

    //private Long quoteId;

    private Integer priority;

    private Integer isDelete = 0;

    private Integer tempUserComment;

    @Formula("(select count(ncl.id) from novel_comment_likes ncl where ncl.novel_comment_id = id)")
    private Long likesCount = 0L;

    @Formula("(select count(ncl.id) from novel_comment_dislikes ncl where ncl.novel_comment_id = id)")
    private Long dislikesCount = 0L;

    @Transient
    @JsonSerialize
    private Boolean liked = false;

    @Transient
    @JsonSerialize
    private Boolean disliked = false;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @CreationTimestamp
    private Date createdTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @UpdateTimestamp
    private Date updatedTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="novelId",referencedColumnName = "id")
    //@JsonBackReference(value = "novel-novelComments")
    @JsonIgnore
    private Novel novel;

    @OneToMany(cascade = {CascadeType.REMOVE},mappedBy="novelComment",fetch = FetchType.LAZY)
    //@JsonManagedReference(value = "novel-novelCommentLikes")
    @JsonIgnore
    private Set<NovelCommentLikes> novelCommentLikes = new HashSet<>();

    @OneToMany(cascade = {CascadeType.REMOVE},mappedBy="novelComment",fetch = FetchType.LAZY)
    //@JsonManagedReference(value = "novel-novelCommentDislikes")
    @JsonIgnore
    private Set<NovelCommentDislikes> novelCommentDislikes = new HashSet<>();

    @ManyToOne()
    @JoinColumn(name="userId",referencedColumnName = "id")
    //@JsonBackReference(value = "user-novelComments")
    private UserLogin userLogin;

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof NovelComment)) {
            return false;
        }

        return id != null && id.equals(((NovelComment) obj).id);
    }

    @Override
    public int hashCode() {
        return 2018;
    }
}
