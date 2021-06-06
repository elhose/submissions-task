package com.recruitment.submission.service;

import com.recruitment.submission.dto.RejectionDTO;
import com.recruitment.submission.dto.SubmissionDTO;

public interface SubmissionService {
    void createSubmission(SubmissionDTO submissionDTO);
    void verifySubmission(SubmissionDTO updatedContent);
    void deleteSubmission(RejectionDTO rejectionDTO);
    void rejectSubmission(RejectionDTO rejectionDTO);
    void acceptSubmission(String title);
}
