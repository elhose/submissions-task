package com.recruitment.submission.service;

import com.recruitment.submission.dto.PresentationDTO;
import com.recruitment.submission.dto.RejectionDTO;
import com.recruitment.submission.dto.SubmissionDTO;
import com.recruitment.submission.entity.Submission;
import com.recruitment.submission.entity.SubmissionStatus;
import com.recruitment.submission.mapper.SubmissionDTOMapper;
import com.recruitment.submission.repository.SubmissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.stream.Collectors;

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

    @Override
    public void verifySubmission(SubmissionDTO updatedContent) {
        checkForNull(updatedContent.getContent());
        Optional<Submission> found = submissionRepository.findByTitleAndStatus(updatedContent.getTitle(), SubmissionStatus.CREATED);
        found.ifPresentOrElse(foundSubmission -> {
            var historySubmission = mapper.entityToHistory(foundSubmission);
            foundSubmission.setContent(updatedContent.getContent());
            foundSubmission.getHistory().add(historySubmission);
            foundSubmission.setStatus(SubmissionStatus.VERIFIED);
            submissionRepository.save(foundSubmission);
        }, () -> throwNewIllegalArgumentException(updatedContent.getTitle(), SubmissionStatus.CREATED));
    }

    @Override
    public void deleteSubmission(RejectionDTO rejectionDTO) {
        checkForNull(rejectionDTO.getReason());
        Optional<Submission> found = submissionRepository.findByTitleAndStatus(rejectionDTO.getTitle(), SubmissionStatus.CREATED);
        found.ifPresentOrElse(foundSubmission -> {
            var historySubmission = mapper.entityToHistory(foundSubmission);
            foundSubmission.setReason(rejectionDTO.getReason());
            foundSubmission.getHistory().add(historySubmission);
            foundSubmission.setStatus(SubmissionStatus.DELETED);
            submissionRepository.save(foundSubmission);
        }, () -> throwNewIllegalArgumentException(rejectionDTO.getTitle(), SubmissionStatus.CREATED));
    }

    @Override
    public void rejectSubmission(RejectionDTO rejectionDTO) {
        checkForNull(rejectionDTO.getReason());
        var requiredStatuses = new SubmissionStatus[]{SubmissionStatus.VERIFIED, SubmissionStatus.ACCEPTED};
        Optional<Submission> found = submissionRepository.findByTitleAndStatusIn(rejectionDTO.getTitle(), Arrays.asList(requiredStatuses));
        found.ifPresentOrElse(foundSubmission -> {
            var historySubmission = mapper.entityToHistory(foundSubmission);
            foundSubmission.setReason(rejectionDTO.getReason());
            foundSubmission.getHistory().add(historySubmission);
            foundSubmission.setStatus(SubmissionStatus.REJECTED);
            submissionRepository.save(foundSubmission);
        }, () -> throwNewIllegalArgumentException(rejectionDTO.getTitle(), requiredStatuses));
    }

    @Override
    public void acceptSubmission(String title) {
        Optional<Submission> found = submissionRepository.findByTitleAndStatus(title, SubmissionStatus.VERIFIED);
        found.ifPresentOrElse(foundSubmission -> {
            var historySubmission = mapper.entityToHistory(foundSubmission);
            foundSubmission.getHistory().add(historySubmission);
            foundSubmission.setStatus(SubmissionStatus.ACCEPTED);
            submissionRepository.save(foundSubmission);
        }, () -> throwNewIllegalArgumentException(title, SubmissionStatus.VERIFIED));
    }

    @Override
    public void publishSubmission(String title) {
        Optional<Submission> found = submissionRepository.findByTitleAndStatus(title, SubmissionStatus.ACCEPTED);
        found.ifPresentOrElse(foundSubmission -> {
            var historySubmission = mapper.entityToHistory(foundSubmission);
            foundSubmission.getHistory().add(historySubmission);
            foundSubmission.setStatus(SubmissionStatus.PUBLISHED);
            foundSubmission.setPublicId(generatePublicId());
            submissionRepository.save(foundSubmission);
        }, () -> throwNewIllegalArgumentException(title, SubmissionStatus.ACCEPTED));
    }

    @Override
    public List<PresentationDTO> listActualSubmissions(String title, SubmissionStatus status) {
        var sort = Sort.by("title").ascending().and(Sort.by("status").ascending());
        List<Submission> foundEntities = submissionRepository.findAll(titleAndStatusSpec(title, status), sort);
        return foundEntities.stream().map(mapper::entityToPresentation).collect(Collectors.toList());
    }

    private Specification<Submission> titleAndStatusSpec(String title, SubmissionStatus submissionStatus) {
        return ((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (title != null && !title.isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("title"), "%" + title + "%"));
            }
            if (submissionStatus != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), submissionStatus));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[]{}));
        });
    }

    @Override
    public List<PresentationDTO> getHistory(String submissionTitle) {
        Optional<Submission> found = submissionRepository.findByTitle(submissionTitle);
        List<PresentationDTO> dtos = new ArrayList<>();
        found.ifPresentOrElse(foundSubmission -> {
            foundSubmission.getHistory().stream()
                           .map(mapper::historyToPresentation)
                           .collect(Collectors.toCollection(() -> dtos));
            dtos.add(mapper.entityToPresentation(foundSubmission));
        }, () -> throwNewIllegalArgumentException(submissionTitle));
        return dtos;
    }

    private void checkForNull(String parameter) {
        if (parameter == null || parameter.isEmpty()) {
            throw new IllegalArgumentException("Submission body doesn't have necessary fields!");
        }
    }

    private void throwNewIllegalArgumentException(String title, SubmissionStatus... statuses) {
        if (statuses == null) {
            throw new IllegalArgumentException("Submission with provided title: " + title + " doesn't exist!");
        }
        throw new IllegalArgumentException("Submission with provided title: " + title + " doesn't exists or doesn't have status in" + Arrays.toString(statuses) + " set!");
    }

    private long generatePublicId() {
        return ByteBuffer.wrap(UUID.randomUUID().toString().getBytes()).asLongBuffer().get();
    }
}
