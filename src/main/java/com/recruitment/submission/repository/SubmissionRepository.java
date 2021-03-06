package com.recruitment.submission.repository;

import com.recruitment.submission.entity.Submission;
import com.recruitment.submission.entity.SubmissionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long>, JpaSpecificationExecutor<Submission> {
    Optional<Submission> findByTitleAndStatus(String title, SubmissionStatus status);
    Optional<Submission> findByTitleAndStatusIn(String title, List<SubmissionStatus> statuses);
    Optional<Submission> findByTitle(String title);
}
