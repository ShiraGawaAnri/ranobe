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
@Table(name = "novel_categories")
@Getter
@Setter
@DynamicUpdate
public class NovelCategories {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @OneToMany(mappedBy="novelCategories")
    //@JsonManagedReference(value = "novelCategories")
    @JsonIgnore
    private Set<Novel> novel = new HashSet<>();

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof NovelCategories)) {
            return false;
        }

        return id != null && id.equals(((NovelCategories) obj).id);
    }

    @Override
    public int hashCode() {
        return 2018;
    }

}
