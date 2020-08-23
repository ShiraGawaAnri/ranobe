package com.kd.novel.backend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Entity
@Table(name = "novel_cover")
@Getter
@Setter
@DynamicUpdate
public class NovelCover {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

//    @Column(nullable = false)
//    private Long novelId;

    @Column(nullable = false)
    private String url;

    private String description;

    @ManyToOne()
    @JoinColumn(name="novelId",referencedColumnName = "id")
    @JsonBackReference(value="novelCovers")
    private Novel novel;


    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof NovelCover)) {
            return false;
        }

        return id != null && id.equals(((NovelCover) obj).id);
    }

    @Override
    public int hashCode() {
        return 2018;
    }
}
