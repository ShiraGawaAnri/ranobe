package com.kd.novel.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "novel_regions")
@Getter
@Setter
@DynamicUpdate
public class NovelRegions {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String area;

    @OneToMany(mappedBy="novelRegions")
    @JsonManagedReference(value = "regions-novelArtist")
    private Set<NovelArtist> novelArtist = new HashSet<>();


    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof NovelRegions)) {
            return false;
        }

        return id != null && id.equals(((NovelRegions) obj).id);
    }

    @Override
    public int hashCode() {
        return 2018;
    }
}
