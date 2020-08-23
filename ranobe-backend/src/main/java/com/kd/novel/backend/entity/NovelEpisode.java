package com.kd.novel.backend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.context.annotation.Lazy;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "novel_episode",uniqueConstraints={
        @UniqueConstraint(columnNames={"episode", "novelId"}),
})
@Getter
@Setter
@DynamicUpdate
public class NovelEpisode {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

//    @Column(nullable = false)
//    private Long novelId;

    @Column(nullable = false)
    private Long episode;

    private String translateTitle;

    private String originTitle;

    private Integer validate;

    @Column(nullable = false)
    private Integer views;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="novelId",referencedColumnName = "id")
    @JsonBackReference(value = "novel-novelEpisodes")
    private Novel novel;

    @OneToMany(cascade = {CascadeType.REMOVE},mappedBy="novelEpisode",fetch = FetchType.EAGER)
    @JsonManagedReference(value = "novelChapters")
    @OrderBy("chapter ASC")
    private Set<NovelChapter> novelChapters = new HashSet<>();

    @ManyToOne()
    @JoinColumn(name="uploaderUserId",referencedColumnName = "id")
    //@JsonBackReference(value = "user-novelEpisodes")
    @JsonIgnore
    private UserLogin userLogin;

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof NovelEpisode)) {
            return false;
        }

        return id != null && id.equals(((NovelEpisode) obj).id);
    }

    @Override
    public int hashCode() {
        return 2018;
    }
}
