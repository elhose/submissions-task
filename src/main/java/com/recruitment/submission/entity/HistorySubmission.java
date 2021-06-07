package com.recruitment.submission.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
public class HistorySubmission {
    @Id
    @GeneratedValue
    @Column(name = "history_id")
    private Long id;
    private String title;
    private String content;
    @Enumerated(EnumType.STRING)
    private SubmissionStatus status;
    private LocalDateTime createDateTime;
    private LocalDateTime updateDateTime;
}
