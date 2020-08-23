package com.kd.novel.backend.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "novel_tags")
@Getter
@Setter
@DynamicUpdate
@JsonIgnoreProperties(value={"hibernateLazyInitializer","handler","fieldHandler"})
public class NovelTags {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false,unique = true)
    private String nameCn;

    @Column(nullable = false,unique = true)
    private String nameJp;

    @Column(nullable = false,unique = true)
    private String nameEn;

    private String description;

    @Column(nullable = false)
    private Integer status;

    private Integer type;

    @OneToMany(mappedBy="novelTags")
    //@JsonManagedReference(value = "novelTag")
    @JsonIgnore
    private Set<NovelTag> novelTag = new HashSet<>();

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof NovelTags)) {
            return false;
        }

        return id != null && id.equals(((NovelTags) obj).id);
    }

    @Override
    public int hashCode() {
        return 2018;
    }
}
