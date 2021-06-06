package com.recruitment.submission.service;

import com.recruitment.submission.dto.SubmissionDTO;
import com.recruitment.submission.mapper.SubmissionDTOMapper;
import com.recruitment.submission.repository.SubmissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SubmissionServiceImpl implements SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final SubmissionDTOMapper mapper;

    @Override
    public void createSubmission(SubmissionDTO submissionDTO) {
        var foundSubmission = mapper.dtoToEntity(submissionDTO);
        submissionRepository.save(foundSubmission);
    }
}
