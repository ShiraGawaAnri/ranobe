package com.kd.novel.backend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Entity
@Table(name = "novel_history",uniqueConstraints={
        @UniqueConstraint(columnNames={"novelId", "userId"}),
})
@Getter
@Setter
@DynamicUpdate
public class NovelHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

//    private Long novelId;
//
//    private Long episodeId;

//    private Long chapterId;

    private Long readWordCount;

    private Long readParagraph;

    private Long readPage;

    private Long lastReadTime;

    @ManyToOne()
    @JoinColumn(name="novelId",referencedColumnName = "id")
    //@JsonBackReference(value = "novel-novelHistories")
    private Novel novel;

    @ManyToOne
    @JoinColumn(name="chapterId",referencedColumnName = "id")
    //@JsonBackReference(value = "novelChapter-novelHistories")
    private NovelChapter novelChapter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="userId",referencedColumnName = "id")
    @JsonBackReference(value = "user-novelHistories")
    private UserLogin userLogin;


    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof NovelHistory)) {
            return false;
        }

        return id != null && id.equals(((NovelHistory) obj).id);
    }

    @Override
    public int hashCode() {
        return 2018;
    }
}
