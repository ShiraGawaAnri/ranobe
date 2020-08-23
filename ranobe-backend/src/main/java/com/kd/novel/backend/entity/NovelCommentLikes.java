package com.kd.novel.backend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "novel_comment_likes")
@Getter
@Setter
@DynamicUpdate
public class NovelCommentLikes {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="novelCommentId",referencedColumnName = "id")
    //@JsonBackReference(value = "novel-novelLikes")
    private NovelComment novelComment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="userId",referencedColumnName = "id")
    @JsonBackReference(value = "user-novelCommentLikes")
    private UserLogin userLogin;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @CreationTimestamp
    private Date createdTime;
}
