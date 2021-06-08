package com.recruitment.submission.dto;

import com.recruitment.submission.entity.SubmissionStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PresentationDTO {
    private String title;
    private String content;
    private SubmissionStatus status;
    private String reason;
    private Long publicId;
    private LocalDateTime createDateTime;
    private LocalDateTime updateDateTime;
}
