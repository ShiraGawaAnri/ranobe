package com.kd.novel.backend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "novel_chapter_paragraph",uniqueConstraints={
        @UniqueConstraint(columnNames={"num", "chapterId"}),
})
@Getter
@Setter
@DynamicUpdate
public class NovelChapterParagraph {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Integer num;

    @Lob
    //无效?
    //@Basic(fetch = FetchType.LAZY)
    @Basic(fetch=FetchType.LAZY,optional=true)
    private String translateContent;

    @Lob
    //@Basic(fetch = FetchType.LAZY)
    @Basic(fetch=FetchType.LAZY,optional=true)
    private String originContent;

    private String imgUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="chapterId",referencedColumnName = "id")
    //@JsonBackReference(value = "novelChapterParagraphs")
    @JsonIgnore
    private NovelChapter novelChapter;

    @OneToMany(cascade = {CascadeType.REMOVE},mappedBy="novelChapterParagraph")
    @JsonManagedReference(value = "novel-novelChapterParagraphComments")
    private Set<NovelChapterParagraphComment> novelChapterParagraphComments = new HashSet<>();

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof NovelChapterParagraph)) {
            return false;
        }

        return id != null && id.equals(((NovelChapterParagraph) obj).id);
    }

    @Override
    public int hashCode() {
        return 2018;
    }
}
