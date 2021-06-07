package com.recruitment.submission.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Table(name = "current_submission")
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "submission_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String title;

    @Column(nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    private SubmissionStatus status = SubmissionStatus.CREATED;

    private String reason;

    private Long publicId;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "fk", referencedColumnName = "submission_id")
    private Set<HistorySubmission> history = new HashSet<>();

    @CreationTimestamp
    private LocalDateTime createDateTime;

    @UpdateTimestamp
    private LocalDateTime updateDateTime;
}
