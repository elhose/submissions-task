package com.recruitment.submission.mapper;

import com.recruitment.submission.dto.PresentationDTO;
import com.recruitment.submission.dto.SubmissionDTO;
import com.recruitment.submission.entity.HistorySubmission;
import com.recruitment.submission.entity.Submission;
import com.recruitment.submission.initializer.Postgres;
import com.recruitment.submission.utils.EntityDTOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ContextConfiguration(initializers = {Postgres.Initializer.class})
class SubmissionDTOMapperTest {

    @Autowired
    private SubmissionDTOMapper mapper;

    @Test
    void testDtoToEntityConversion() {
        SubmissionDTO submissionDTO = EntityDTOUtils.prepareSubmissionDTO();
        Submission submission = mapper.dtoToEntity(submissionDTO);
        assertEquals(submissionDTO.getTitle(), submission.getTitle());
        assertEquals(submissionDTO.getContent(), submission.getContent());
    }

    @Test
    void testEntityToHistoryConversion() {
        Submission submission = EntityDTOUtils.prepareSubmission();
        HistorySubmission historySubmission = mapper.entityToHistory(submission);
        assertEquals(submission.getId(), historySubmission.getId());
        assertEquals(submission.getTitle(), historySubmission.getTitle());
        assertEquals(submission.getContent(), historySubmission.getContent());
        assertEquals(submission.getStatus(), historySubmission.getStatus());
        assertEquals(submission.getCreateDateTime(), historySubmission.getCreateDateTime());
        assertEquals(submission.getUpdateDateTime(), historySubmission.getUpdateDateTime());
    }

    @Test
    void testEntityToPresentation() {
        Submission submission = EntityDTOUtils.prepareSubmission();
        PresentationDTO presentationDTO = mapper.entityToPresentation(submission);
        assertEquals(submission.getTitle(), presentationDTO.getTitle());
        assertEquals(submission.getContent(), presentationDTO.getContent());
        assertEquals(submission.getStatus(), presentationDTO.getStatus());
        assertEquals(submission.getReason(), presentationDTO.getReason());
        assertEquals(submission.getPublicId(), presentationDTO.getPublicId());
        assertEquals(submission.getCreateDateTime(), presentationDTO.getCreateDateTime());
        assertEquals(submission.getUpdateDateTime(), presentationDTO.getUpdateDateTime());
    }

    @Test
    void testHistoryToPresentation() {
        HistorySubmission historySubmission = EntityDTOUtils.prepareHistorySubmission();
        PresentationDTO presentationDTO = mapper.historyToPresentation(historySubmission);
        assertEquals(historySubmission.getTitle(), presentationDTO.getTitle());
        assertEquals(historySubmission.getContent(), presentationDTO.getContent());
        assertEquals(historySubmission.getStatus(), presentationDTO.getStatus());
        assertEquals(historySubmission.getCreateDateTime(), presentationDTO.getCreateDateTime());
        assertEquals(historySubmission.getUpdateDateTime(), presentationDTO.getUpdateDateTime());
    }
}