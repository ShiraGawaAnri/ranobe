package com.kd.novel.backend.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "novel_chapter_comment")
@Getter
@Setter
@DynamicUpdate
public class NovelChapterComment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Lob
    @Column(nullable = false,length = 2000)
    private String mes;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @CreationTimestamp
    private Date createdTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @UpdateTimestamp
    private Date updatedTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="chapterId",referencedColumnName = "id")
    @JsonBackReference(value = "novel-novelChapterComments")
    private NovelChapter novelChapter;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="userId",referencedColumnName = "id")
    @JsonBackReference(value = "user-novelChapterComments")
    private UserLogin userLogin;

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof NovelChapterComment)) {
            return false;
        }

        return id != null && id.equals(((NovelChapterComment) obj).id);
    }

    @Override
    public int hashCode() {
        return 2018;
    }
}
