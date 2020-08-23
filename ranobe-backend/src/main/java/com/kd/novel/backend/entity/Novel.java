package com.kd.novel.backend.entity;

import com.fasterxml.jackson.annotation.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.context.annotation.Lazy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "novel")
@Getter
@Setter
@DynamicUpdate
public class Novel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

//    @Column(nullable = false)
//    private Long categoryId;

//    @Column(nullable = false)
//    private Integer artistId;

    @Column(nullable = false)
    private String translateTitle;

    @Column(nullable = false)
    private String originTitle;

    @Lob
    @Column(nullable = false)
    private String introduction;

    @Column(nullable = false)
    private Integer views;

    @Column(nullable = false)
    private Integer isDelete;

    @Column(nullable = false)
    private Integer validate;

    private Integer finish;

    private Integer locked;

    @Column(columnDefinition = "bigint DEFAULT 0")
    private Long likesCount;

    @Column(columnDefinition = "bigint DEFAULT 0")
    private Long subscribesCount;

    @Column(columnDefinition = "bigint DEFAULT 0")
    private Long commentsCount;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @CreationTimestamp
    private Date createdTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @UpdateTimestamp
    private Date updatedTime;

    private Long lastChapterUpdatedTime;

    @ManyToOne()
    @JoinColumn(name="uploaderUserId",referencedColumnName = "id")
    @JsonBackReference(value = "novels")
    private UserLogin userLogin;

    @OneToMany(cascade = {CascadeType.REMOVE},mappedBy="novel",fetch = FetchType.EAGER)
    @JsonManagedReference(value = "novelTags")
    private Set<NovelTag> novelTags = new HashSet<>();

    @ManyToOne()
    @JoinColumn(name="artistId",referencedColumnName = "id")
    //@JsonBackReference(value = "novelArtist")
    private NovelArtist novelArtist;

    @ManyToOne()
    @JoinColumn(name="categoryId",referencedColumnName = "id")
    //@JsonBackReference(value = "novelCategories")
    private NovelCategories novelCategories;

    @OneToMany(cascade = {CascadeType.REMOVE},mappedBy="novel",fetch = FetchType.EAGER)
    @JsonManagedReference(value = "novelCovers")
    //@EqualsAndHashCode.Exclude @ToString.Exclude
    private Set<NovelCover> novelCovers  = new HashSet<>();

    @OneToMany(cascade = {CascadeType.REMOVE},mappedBy="novel",fetch = FetchType.LAZY)
    //@JsonManagedReference(value = "novel-novelSubscribes")
    @JsonIgnore
    private Set<NovelSubscribe> novelSubscribes = new HashSet<>();

    @OneToMany(cascade = {CascadeType.REMOVE},mappedBy="novel",fetch = FetchType.EAGER)
    @JsonManagedReference(value = "novel-novelEpisodes")
    @OrderBy("episode ASC")
    private Set<NovelEpisode> novelEpisodes = new HashSet<>();

    @OneToMany(cascade = {CascadeType.REMOVE},mappedBy="novel",fetch = FetchType.LAZY)
    //@JsonManagedReference(value = "novel-novelComments")
    private Set<NovelComment> novelComments = new HashSet<>();

    @OneToMany(cascade = {CascadeType.REMOVE},mappedBy="novel",fetch = FetchType.LAZY)
    //@JsonManagedReference(value = "novel-novelHistories")
    @JsonIgnore
    private Set<NovelHistory> novelHistories = new HashSet<>();

    @OneToMany(cascade = {CascadeType.REMOVE},mappedBy="novel",fetch = FetchType.LAZY)
    //@JsonManagedReference(value = "novel-novelLikes")
    @JsonIgnore
    private Set<NovelLikes> novelLikes = new HashSet<>();

    @Version
    @Column(columnDefinition = "bigint DEFAULT 0")
    private Long version;

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Novel)) {
            return false;
        }

        return id != null && id.equals(((Novel) obj).id);
    }

    @Override
    public int hashCode() {
        return 2018;
    }

    @Override
    public String toString() {
        return "Novel{" +
                "id=" + id +
                '}';
    }
}
