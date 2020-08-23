package com.kd.novel.backend.entity;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Entity
@Table(name = "novel_comment_quote")
@Getter
@Setter
@DynamicUpdate
public class NovelCommentQuote {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long novelCommentId;

    private Long parentId;

    private Integer isDelete;
}
