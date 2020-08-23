package com.kd.novel.backend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "novel_artist")
@Getter
@Setter
@DynamicUpdate
public class NovelArtist {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String translateName;

    @Column(nullable = false)
    private String originName;

    @Column(nullable = false)
    private Integer status;

    private String comment;

    private String link;

    @Column(nullable = false)
    private Integer isDelete = 0;

    @ManyToOne()
    @JoinColumn(name="regionId",referencedColumnName = "id")
    @JsonBackReference(value = "regions-novelArtist")
    private NovelRegions novelRegions;

    @OneToMany(mappedBy="novelArtist")
    //@JsonManagedReference(value = "novelArtist")
    @JsonIgnore
    private Set<Novel> novel = new HashSet<>();

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof NovelArtist)) {
            return false;
        }

        return id != null && id.equals(((NovelArtist) obj).id);
    }

    @Override
    public int hashCode() {
        return 2018;
    }
}
