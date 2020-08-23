package com.kd.novel.backend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Entity
@Table(name = "novel_subscribe")
@Getter
@Setter
@DynamicUpdate
public class NovelSubscribe {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

//    @Column(nullable = false)
//    private Long novelId;

//    @Column(nullable = false)
//    private Long userId;

    private Long subscribeTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="userId",referencedColumnName = "id")
    //@JsonBackReference(value = "user-novelSubscribes")
    @JsonIgnore
    private UserLogin userLogin;

    @ManyToOne()
    @JoinColumn(name="novelId",referencedColumnName = "id")
    //@JsonBackReference(value = "novel-novelSubscribes")
    private Novel novel;

    private Integer hasNew = 0;

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof NovelSubscribe)) {
            return false;
        }

        return id != null && id.equals(((NovelSubscribe) obj).id);
    }

    @Override
    public int hashCode() {
        return 2018;
    }
}
