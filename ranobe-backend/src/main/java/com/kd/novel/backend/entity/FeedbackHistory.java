package com.kd.novel.backend.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kd.novel.backend.dto.UserLoginDTO;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "feedback_history")
@Getter
@Setter
@DynamicUpdate
public class FeedbackHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String reportUrl;

    @Column(length = 5000)
    @JsonIgnore
    private String reportData;

    @Column(nullable = false)
    private String reportReason;

    private Integer reportReasonNum;

    @Column(nullable = false)
    private String reportMes;

    private String replyMes;

    private Integer status = 0;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @CreationTimestamp
    private Date createdTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @UpdateTimestamp
    private Date updatedTime;

    @ManyToOne
    @JoinColumn(name="userId",referencedColumnName = "id")
    //@JsonBackReference(value = "userFeedbackHistories")
    private UserLogin userLogin;

//    @ManyToOne(cascade = CascadeType.ALL)
//    @JoinColumn(name="operatorUserId",referencedColumnName = "id")
//    //@JsonBackReference(value = "operatorFeedbackHistories")
//    private UserLogin operatorUserLogin;

}
