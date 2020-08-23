package com.kd.novel.backend.entity;

import com.fasterxml.jackson.annotation.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "novel_chapter_paragraph_comment")
@Getter
@Setter
@DynamicUpdate
@JsonIgnoreProperties(value={"hibernateLazyInitializer","handler","fieldHandler"})
public class NovelChapterParagraphComment {

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
    @JoinColumn(name="chapterParagraphId",referencedColumnName = "id")
    @JsonBackReference(value = "novel-novelChapterParagraphComments")
    private NovelChapterParagraph novelChapterParagraph;

    @ManyToOne()
    @JoinColumn(name="userId",referencedColumnName = "id")
    //@JsonBackReference(value = "user-novelChapterParagraphComments")
    private UserLogin userLogin;

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof NovelChapterParagraphComment)) {
            return false;
        }

        return id != null && id.equals(((NovelChapterParagraphComment) obj).id);
    }

    @Override
    public int hashCode() {
        return 2018;
    }
}
