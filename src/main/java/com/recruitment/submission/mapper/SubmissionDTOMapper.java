package com.recruitment.submission.mapper;

import com.recruitment.submission.dto.SubmissionDTO;
import com.recruitment.submission.entity.HistorySubmission;
import com.recruitment.submission.entity.Submission;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface SubmissionDTOMapper {

    Submission dtoToEntity(SubmissionDTO dto);

    HistorySubmission entityToHistory(Submission submission);
}
