package com.kd.novel.backend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.context.annotation.Lazy;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "novel_chapter")
@Getter
@Setter
@DynamicUpdate
public class NovelChapter {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

//    @Column(nullable = false)
//    private Long novelId;

    @Column(nullable = false)
    private Long chapter;

    private String translateTitle;

    private String originTitle;

    private Long wordCount;

//    private String translateContent;
//
//    private String originContent;

    private String headerInfo;

    private String footerInfo;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @CreationTimestamp
    private Date createdTime;

    @OneToMany(cascade = {CascadeType.REMOVE},mappedBy="novelChapter")
    //@JsonManagedReference(value = "novelChapterParagraphs")
    @OrderBy("num asc")
    private Set<NovelChapterParagraph> novelChapterParagraphs = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="episodeId",referencedColumnName = "id")
    @JsonBackReference(value = "novelChapters")
    private NovelEpisode novelEpisode;

    @OneToMany(cascade = {CascadeType.REMOVE},mappedBy="novelChapter")
    @JsonManagedReference(value = "novel-novelChapterComments")
    private Set<NovelChapterComment> novelChapterComments = new HashSet<>();

    @OneToMany(cascade = {CascadeType.REMOVE},mappedBy="novelChapter")
    //@JsonManagedReference(value = "novelHistories")
    @JsonIgnore
    private Set<NovelHistory> novelHistories = new HashSet<>();

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof NovelChapter)) {
            return false;
        }

        return id != null && id.equals(((NovelChapter) obj).id);
    }

    @Override
    public int hashCode() {
        return 2018;
    }
}
