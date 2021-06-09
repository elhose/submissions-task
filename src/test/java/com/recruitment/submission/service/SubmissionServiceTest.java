package com.recruitment.submission.service;

import com.recruitment.submission.dto.PresentationDTO;
import com.recruitment.submission.dto.RejectionDTO;
import com.recruitment.submission.dto.SubmissionDTO;
import com.recruitment.submission.entity.HistorySubmission;
import com.recruitment.submission.entity.Submission;
import com.recruitment.submission.entity.SubmissionStatus;
import com.recruitment.submission.initializer.Postgres;
import com.recruitment.submission.mapper.SubmissionDTOMapper;
import com.recruitment.submission.repository.SubmissionRepository;
import com.recruitment.submission.utils.EntityDTOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ContextConfiguration;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest()
@ContextConfiguration(initializers = {Postgres.Initializer.class})
class SubmissionServiceTest {

    @MockBean
    private SubmissionDTOMapper mapper;

    @MockBean
    private SubmissionRepository repository;

    @Autowired
    private SubmissionService submissionService;

    @Test
    void testCreateSubmission() {
        SubmissionDTO submissionDTO = EntityDTOUtils.prepareSubmissionDTO();
        Submission submission = EntityDTOUtils.prepareSubmission();

        Mockito.when(mapper.dtoToEntity(submissionDTO)).thenReturn(submission);
        Mockito.when(repository.save(submission)).thenReturn(submission);

        submissionService.createSubmission(submissionDTO);

        InOrder order = Mockito.inOrder(mapper, repository);
        order.verify(mapper, times(1)).dtoToEntity(submissionDTO);
        order.verify(repository, times(1)).save(submission);
    }

    @Test
    void testVerifySubmission() {
        SubmissionDTO submissionDTO = EntityDTOUtils.prepareSubmissionDTO();
        Submission startSubmission = EntityDTOUtils.prepareSubmission(submissionDTO.getTitle(), SubmissionStatus.CREATED);
        Submission finalSubmission = EntityDTOUtils.prepareSubmission(submissionDTO.getTitle(), SubmissionStatus.VERIFIED);
        HistorySubmission historySubmission = EntityDTOUtils.prepareHistorySubmission(submissionDTO.getTitle(), SubmissionStatus.CREATED);
        finalSubmission.setContent(submissionDTO.getContent());
        finalSubmission.getHistory().add(historySubmission);

        Mockito.when(repository.findByTitleAndStatus(submissionDTO.getTitle(), SubmissionStatus.CREATED)).thenReturn(Optional.of(startSubmission));
        Mockito.when(mapper.entityToHistory(startSubmission)).thenReturn(historySubmission);
        Mockito.when(repository.save(finalSubmission)).thenReturn(finalSubmission);

        submissionService.verifySubmission(submissionDTO);

        InOrder order = Mockito.inOrder(mapper, repository);
        order.verify(repository, times(1)).findByTitleAndStatus(submissionDTO.getTitle(), SubmissionStatus.CREATED);
        order.verify(mapper, times(1)).entityToHistory(startSubmission);
        order.verify(repository, times(1)).save(startSubmission);
        assertTrue(finalSubmission.getHistory().contains(historySubmission));
        assertEquals(SubmissionStatus.VERIFIED, finalSubmission.getStatus());
        assertEquals(submissionDTO.getContent(), finalSubmission.getContent());
    }

    @Test
    void testVerifySubmissionException() {
        SubmissionDTO submissionDTO = EntityDTOUtils.prepareSubmissionDTO();

        Mockito.when(repository.findByTitleAndStatus(submissionDTO.getTitle(), SubmissionStatus.CREATED)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> submissionService.verifySubmission(submissionDTO));
        verify(repository, times(1)).findByTitleAndStatus(submissionDTO.getTitle(), SubmissionStatus.CREATED);
    }

    @Test
    void testVerifySubmissionNullParameter() {
        SubmissionDTO submissionDTO = EntityDTOUtils.prepareSubmissionDTO();
        submissionDTO.setContent(null);

        assertThrows(IllegalArgumentException.class, () -> submissionService.verifySubmission(submissionDTO));
    }

    @Test
    void testDeleteSubmission() {
        RejectionDTO rejectionDTO = EntityDTOUtils.prepareRejectionDTO();
        Submission startSubmission = EntityDTOUtils.prepareSubmission(rejectionDTO.getTitle(), SubmissionStatus.CREATED);
        Submission finalSubmission = EntityDTOUtils.prepareSubmission(rejectionDTO.getTitle(), SubmissionStatus.DELETED);
        HistorySubmission historySubmission = EntityDTOUtils.prepareHistorySubmission(rejectionDTO.getTitle(), SubmissionStatus.CREATED);
        finalSubmission.setReason(rejectionDTO.getReason());
        finalSubmission.getHistory().add(historySubmission);

        Mockito.when(repository.findByTitleAndStatus(rejectionDTO.getTitle(), SubmissionStatus.CREATED)).thenReturn(Optional.of(startSubmission));
        Mockito.when(mapper.entityToHistory(startSubmission)).thenReturn(historySubmission);
        Mockito.when(repository.save(finalSubmission)).thenReturn(finalSubmission);

        submissionService.deleteSubmission(rejectionDTO);

        InOrder order = Mockito.inOrder(mapper, repository);
        order.verify(repository, times(1)).findByTitleAndStatus(rejectionDTO.getTitle(), SubmissionStatus.CREATED);
        order.verify(mapper, times(1)).entityToHistory(startSubmission);
        order.verify(repository, times(1)).save(startSubmission);
        assertTrue(finalSubmission.getHistory().contains(historySubmission));
        assertEquals(SubmissionStatus.DELETED, finalSubmission.getStatus());
        assertEquals(rejectionDTO.getReason(), finalSubmission.getReason());
    }

    @Test
    void testDeleteSubmissionException() {
        RejectionDTO rejectionDTO = EntityDTOUtils.prepareRejectionDTO();

        Mockito.when(repository.findByTitleAndStatus(rejectionDTO.getTitle(), SubmissionStatus.CREATED)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> submissionService.deleteSubmission(rejectionDTO));
        verify(repository, times(1)).findByTitleAndStatus(rejectionDTO.getTitle(), SubmissionStatus.CREATED);
    }

    @Test
    void testDeleteSubmissionNullParameter() {
        RejectionDTO rejectionDTO = EntityDTOUtils.prepareRejectionDTO();
        rejectionDTO.setReason(null);

        assertThrows(IllegalArgumentException.class, () -> submissionService.rejectSubmission(rejectionDTO));
    }

    @Test
    void testRejectSubmission() {
        var requiredStatuses = new SubmissionStatus[]{SubmissionStatus.VERIFIED, SubmissionStatus.ACCEPTED};
        RejectionDTO rejectionDTO = EntityDTOUtils.prepareRejectionDTO();
        Submission startSubmission = EntityDTOUtils.prepareSubmission(rejectionDTO.getTitle(), SubmissionStatus.VERIFIED);
        Submission finalSubmission = EntityDTOUtils.prepareSubmission(rejectionDTO.getTitle(), SubmissionStatus.REJECTED);
        HistorySubmission historySubmission = EntityDTOUtils.prepareHistorySubmission(rejectionDTO.getTitle(), SubmissionStatus.VERIFIED);
        finalSubmission.setReason(rejectionDTO.getReason());
        finalSubmission.getHistory().add(historySubmission);

        Mockito.when(repository.findByTitleAndStatusIn(rejectionDTO.getTitle(), Arrays.asList(requiredStatuses))).thenReturn(Optional.of(startSubmission));
        Mockito.when(mapper.entityToHistory(startSubmission)).thenReturn(historySubmission);
        Mockito.when(repository.save(finalSubmission)).thenReturn(finalSubmission);

        submissionService.rejectSubmission(rejectionDTO);

        InOrder order = Mockito.inOrder(mapper, repository);
        order.verify(repository, times(1)).findByTitleAndStatusIn(rejectionDTO.getTitle(), Arrays.asList(requiredStatuses));
        order.verify(mapper, times(1)).entityToHistory(startSubmission);
        order.verify(repository, times(1)).save(startSubmission);
        assertTrue(finalSubmission.getHistory().contains(historySubmission));
        assertEquals(SubmissionStatus.REJECTED, finalSubmission.getStatus());
        assertEquals(rejectionDTO.getReason(), finalSubmission.getReason());
    }

    @Test
    void testRejectSubmissionException() {
        var requiredStatuses = new SubmissionStatus[]{SubmissionStatus.VERIFIED, SubmissionStatus.ACCEPTED};
        RejectionDTO rejectionDTO = EntityDTOUtils.prepareRejectionDTO();

        Mockito.when(repository.findByTitleAndStatusIn(rejectionDTO.getTitle(), Arrays.asList(requiredStatuses))).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> submissionService.rejectSubmission(rejectionDTO));
        verify(repository, times(1)).findByTitleAndStatusIn(rejectionDTO.getTitle(), Arrays.asList(requiredStatuses));
    }

    @Test
    void testRejectSubmissionNullParameter() {
        RejectionDTO rejectionDTO = EntityDTOUtils.prepareRejectionDTO();
        rejectionDTO.setReason(null);

        assertThrows(IllegalArgumentException.class, () -> submissionService.rejectSubmission(rejectionDTO));
    }

    @Test
    void testAcceptSubmission() {
        String title = "testTitle1";
        Submission startSubmission = EntityDTOUtils.prepareSubmission(title, SubmissionStatus.VERIFIED);
        Submission finalSubmission = EntityDTOUtils.prepareSubmission(title, SubmissionStatus.ACCEPTED);
        HistorySubmission historySubmission = EntityDTOUtils.prepareHistorySubmission(title, SubmissionStatus.VERIFIED);
        finalSubmission.getHistory().add(historySubmission);

        Mockito.when(repository.findByTitleAndStatus(title, SubmissionStatus.VERIFIED)).thenReturn(Optional.of(startSubmission));
        Mockito.when(mapper.entityToHistory(startSubmission)).thenReturn(historySubmission);
        Mockito.when(repository.save(finalSubmission)).thenReturn(finalSubmission);

        submissionService.acceptSubmission(title);

        InOrder order = Mockito.inOrder(mapper, repository);
        order.verify(repository, times(1)).findByTitleAndStatus(title, SubmissionStatus.VERIFIED);
        order.verify(mapper, times(1)).entityToHistory(startSubmission);
        order.verify(repository, times(1)).save(startSubmission);
        assertTrue(finalSubmission.getHistory().contains(historySubmission));
        assertEquals(SubmissionStatus.ACCEPTED, finalSubmission.getStatus());
    }

    @Test
    void testAcceptSubmissionException() {
        String title = "testTitle1";

        Mockito.when(repository.findByTitleAndStatus(title, SubmissionStatus.VERIFIED)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> submissionService.acceptSubmission(title));
        verify(repository, times(1)).findByTitleAndStatus(title, SubmissionStatus.VERIFIED);
    }

    @Test
    void testPublishSubmission() {
        String title = "testTitle1";
        Long publicId = 123L;
        Submission startSubmission = EntityDTOUtils.prepareSubmission(title, SubmissionStatus.ACCEPTED);
        Submission finalSubmission = EntityDTOUtils.prepareSubmission(title, SubmissionStatus.PUBLISHED);
        HistorySubmission historySubmission = EntityDTOUtils.prepareHistorySubmission(title, SubmissionStatus.ACCEPTED);
        finalSubmission.getHistory().add(historySubmission);
        finalSubmission.setPublicId(publicId);

        Mockito.when(repository.findByTitleAndStatus(title, SubmissionStatus.ACCEPTED)).thenReturn(Optional.of(startSubmission));
        Mockito.when(mapper.entityToHistory(startSubmission)).thenReturn(historySubmission);
        Mockito.when(repository.save(finalSubmission)).thenReturn(finalSubmission);

        submissionService.publishSubmission(title);

        InOrder order = Mockito.inOrder(mapper, repository);
        order.verify(repository, times(1)).findByTitleAndStatus(title, SubmissionStatus.ACCEPTED);
        order.verify(mapper, times(1)).entityToHistory(startSubmission);
        order.verify(repository, times(1)).save(startSubmission);
        assertTrue(finalSubmission.getHistory().contains(historySubmission));
        assertEquals(SubmissionStatus.PUBLISHED, finalSubmission.getStatus());
        assertEquals(publicId, finalSubmission.getPublicId());
    }

    @Test
    void testPublishSubmissionException() {
        String title = "testTitle1";

        Mockito.when(repository.findByTitleAndStatus(title, SubmissionStatus.ACCEPTED)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> submissionService.publishSubmission(title));
        verify(repository, times(1)).findByTitleAndStatus(title, SubmissionStatus.ACCEPTED);
    }

    @Test
    void testListActualSubmissions() {
        SubmissionStatus status = SubmissionStatus.CREATED;
        String title1 = "test-title1";
        Submission submission1 = EntityDTOUtils.prepareSubmission(title1, status);
        PresentationDTO presentationDTO1 = EntityDTOUtils.preparePresentationDTO(title1, status);
        String title2 = "test-title2";
        Submission submission2 = EntityDTOUtils.prepareSubmission(title2, status);
        PresentationDTO presentationDTO2 = EntityDTOUtils.preparePresentationDTO(title2, status);
        String title3 = "test-title3";
        Submission submission3 = EntityDTOUtils.prepareSubmission(title3, status);
        PresentationDTO presentationDTO3 = EntityDTOUtils.preparePresentationDTO(title3, status);
        List<Submission> submissionList = Arrays.asList(submission1, submission2, submission3);

        Mockito.when(repository.findAll(any(Specification.class), any(Sort.class))).thenReturn(submissionList);
        Mockito.when(mapper.entityToPresentation(submission1)).thenReturn(presentationDTO1);
        Mockito.when(mapper.entityToPresentation(submission2)).thenReturn(presentationDTO2);
        Mockito.when(mapper.entityToPresentation(submission3)).thenReturn(presentationDTO3);

        List<PresentationDTO> presentationDTOS = submissionService.listActualSubmissions(title1, status);

        InOrder order = Mockito.inOrder(mapper, repository);
        order.verify(repository, times(1)).findAll(any(Specification.class), any(Sort.class));
        order.verify(mapper, times(1)).entityToPresentation(submission1);
        order.verify(mapper, times(1)).entityToPresentation(submission2);
        order.verify(mapper, times(1)).entityToPresentation(submission3);
        Assertions.assertEquals(submissionList.size(), presentationDTOS.size());
        Assertions.assertIterableEquals(Arrays.asList(presentationDTO1, presentationDTO2, presentationDTO3), presentationDTOS);
    }

    @Test
    void testGetHistory() {
        String title = "test-title1";
        PresentationDTO presentationDTO1 = EntityDTOUtils.preparePresentationDTO(title, SubmissionStatus.PUBLISHED);
        PresentationDTO presentationDTO2 = EntityDTOUtils.preparePresentationDTO(title, SubmissionStatus.CREATED);
        PresentationDTO presentationDTO3 = EntityDTOUtils.preparePresentationDTO(title, SubmissionStatus.VERIFIED);
        PresentationDTO presentationDTO4 = EntityDTOUtils.preparePresentationDTO(title, SubmissionStatus.ACCEPTED);
        Submission submission = EntityDTOUtils.prepareSubmission(title);
        submission.getHistory().clear();
        HistorySubmission historySubmission2 = EntityDTOUtils.prepareHistorySubmission(title, SubmissionStatus.CREATED);
        HistorySubmission historySubmission3 = EntityDTOUtils.prepareHistorySubmission(title, SubmissionStatus.VERIFIED);
        HistorySubmission historySubmission4 = EntityDTOUtils.prepareHistorySubmission(title, SubmissionStatus.ACCEPTED);
        submission.getHistory().add(historySubmission2);
        submission.getHistory().add(historySubmission3);
        submission.getHistory().add(historySubmission4);

        Mockito.when(repository.findByTitle(title)).thenReturn(Optional.of(submission));
        Mockito.when(mapper.historyToPresentation(historySubmission2)).thenReturn(presentationDTO2);
        Mockito.when(mapper.historyToPresentation(historySubmission3)).thenReturn(presentationDTO3);
        Mockito.when(mapper.historyToPresentation(historySubmission4)).thenReturn(presentationDTO4);
        Mockito.when(mapper.entityToPresentation(submission)).thenReturn(presentationDTO1);

        List<PresentationDTO> history = submissionService.getHistory(title);

        InOrder order = Mockito.inOrder(mapper, repository);
        order.verify(repository, times(1)).findByTitle(title);
        order.verify(mapper, times(1)).historyToPresentation(historySubmission2);
        order.verify(mapper, times(1)).historyToPresentation(historySubmission3);
        order.verify(mapper, times(1)).historyToPresentation(historySubmission4);
        order.verify(mapper, times(1)).entityToPresentation(submission);
        Assertions.assertEquals(4, history.size());
        Assertions.assertEquals(presentationDTO1, history.get(3));
        Assertions.assertEquals(presentationDTO4, history.get(2));
        Assertions.assertEquals(presentationDTO3, history.get(1));
        Assertions.assertEquals(presentationDTO2, history.get(0));
    }

    @Test
    void testGetHistoryTitleNotFound() {
        String title = "test-title1";
        Mockito.when(repository.findByTitle(title)).thenReturn(Optional.empty());

        Assertions.assertThrows(IllegalArgumentException.class, () -> submissionService.getHistory(title));
    }

}