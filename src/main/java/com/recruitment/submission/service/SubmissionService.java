package com.recruitment.submission.service;

import com.recruitment.submission.dto.PresentationDTO;
import com.recruitment.submission.dto.RejectionDTO;
import com.recruitment.submission.dto.SubmissionDTO;
import com.recruitment.submission.entity.SubmissionStatus;

import java.util.List;

public interface SubmissionService {
    void createSubmission(SubmissionDTO submissionDTO);
    void verifySubmission(SubmissionDTO updatedContent);
    void deleteSubmission(RejectionDTO rejectionDTO);
    void rejectSubmission(RejectionDTO rejectionDTO);
    void acceptSubmission(String title);
    void publishSubmission(String title);
    List<PresentationDTO> listActualSubmissions(String title, SubmissionStatus status);
    List<PresentationDTO> getHistory(String submissionTitle);
}
