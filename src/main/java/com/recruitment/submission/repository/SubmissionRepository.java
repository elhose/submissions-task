package com.recruitment.submission.repository;

import com.recruitment.submission.entity.Submission;
import com.recruitment.submission.entity.SubmissionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    Optional<Submission> findByTitleAndStatus(String title, SubmissionStatus status);

}
