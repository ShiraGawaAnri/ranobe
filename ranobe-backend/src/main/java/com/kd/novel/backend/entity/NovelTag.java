package com.kd.novel.backend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Entity
@Table(name = "novel_tag")
@Getter
@Setter
@DynamicUpdate
@JsonIgnoreProperties(value={"hibernateLazyInitializer","handler","fieldHandler"})
public class NovelTag {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

//    @Column(nullable = false)
//    private Long novelId;

//    @Column(nullable = false)
//    private Long tagId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="novelId",referencedColumnName = "id")
    @JsonBackReference(value = "novelTags")
    private Novel novel;

    @ManyToOne()
    @JoinColumn(name="tagId",referencedColumnName = "id")
    //@JsonBackReference(value = "novelTag")
    private NovelTags novelTags;

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof NovelTag)) {
            return false;
        }

        return id != null && id.equals(((NovelTag) obj).id);
    }

    @Override
    public int hashCode() {
        return 2018;
    }
}
