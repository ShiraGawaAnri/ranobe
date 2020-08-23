package com.kd.novel.backend.dto;


import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

@Data
public class FeedbackAddOrUpdateRequest {

    private Long id;

    private String reportUrl;

    private String reportData;

    @NotNull(message = "反馈理由不能为空")
    private String reportReason;

    private Integer reportReasonNum;


    @NotNull(message = "反馈细节不能为空")
    @Length(min = 5,max = 250,message = "反馈细节必须在5-250字之间")
    private String reportMes;
}
