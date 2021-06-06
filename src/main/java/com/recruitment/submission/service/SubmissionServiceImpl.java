package com.recruitment.submission.service;

import com.recruitment.submission.entity.Submission;
import com.recruitment.submission.repository.SubmissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubmissionServiceImpl implements SubmissionService {

    private final SubmissionRepository submissionRepository;

    @Override
    public List<Submission> getAll() {
        return submissionRepository.findAll();
    }
}
