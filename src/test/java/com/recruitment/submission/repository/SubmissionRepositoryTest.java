package com.recruitment.submission.repository;

import com.recruitment.submission.entity.Submission;
import com.recruitment.submission.entity.SubmissionStatus;
import com.recruitment.submission.initializer.Postgres;
import com.recruitment.submission.utils.EntityDTOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ContextConfiguration;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(initializers = {Postgres.Initializer.class})
class SubmissionRepositoryTest {

    @Autowired
    private SubmissionRepository submissionRepository;

    @Test
    void testSaveSubmission() {
        Submission expectedSubmission = EntityDTOUtils.prepareSubmission();
        Submission actualSubmission = submissionRepository.save(expectedSubmission);
        assertEquals(expectedSubmission.getTitle(), actualSubmission.getTitle());
    }

    @Test
    void testDuplicateTitleException() {
        String testTitle = "title1";
        Submission expectedSubmission = EntityDTOUtils.prepareSubmission(testTitle);
        Submission expectedSubmission2 = EntityDTOUtils.prepareSubmission(testTitle);
        submissionRepository.save(expectedSubmission);
        submissionRepository.save(expectedSubmission2);
        assertThrows(DataIntegrityViolationException.class, () -> submissionRepository.findAll());
    }

    @Test
    void testFindByTittle() {
        String searchedTitle = "test-title2";
        Submission savedSubmission1 = EntityDTOUtils.prepareSubmission("test-title1");
        Submission expectedSubmission = EntityDTOUtils.prepareSubmission(searchedTitle);
        Submission savedSubmission3 = EntityDTOUtils.prepareSubmission("test-title3");
        submissionRepository.save(savedSubmission1);
        submissionRepository.save(expectedSubmission);
        submissionRepository.save(savedSubmission3);

        Optional<Submission> foundOptional = submissionRepository.findByTitle(searchedTitle);
        foundOptional.ifPresentOrElse(actualSubmission -> {
            assertTrue(EntityDTOUtils.compareSubmissionsWithoutIdAndDates(expectedSubmission, actualSubmission));
        }, () -> {
            throw new IllegalStateException("Entity not saved correctly!");
        });
    }

    @Test
    void testFindByTitleAndStatus() {
        String searchedTitle = "test-title2";
        SubmissionStatus searchedStatus = SubmissionStatus.ACCEPTED;
        Submission savedSubmission1 = EntityDTOUtils.prepareSubmission("test-title1", SubmissionStatus.VERIFIED);
        Submission expectedSubmission = EntityDTOUtils.prepareSubmission(searchedTitle, searchedStatus);
        Submission savedSubmission3 = EntityDTOUtils.prepareSubmission("test-title3", searchedStatus);
        submissionRepository.save(savedSubmission1);
        submissionRepository.save(expectedSubmission);
        submissionRepository.save(savedSubmission3);

        Optional<Submission> foundOptional = submissionRepository.findByTitleAndStatus(searchedTitle, searchedStatus);
        foundOptional.ifPresentOrElse(actualSubmission -> {
            assertTrue(EntityDTOUtils.compareSubmissionsWithoutIdAndDates(expectedSubmission, actualSubmission));
        }, () -> {
            throw new IllegalStateException("Entity not saved correctly!");
        });
    }

    @Test
    void testFindByTitleAndStatusIn() {
        String searchedTitle = "test-title2";
        var requiredStatuses = new SubmissionStatus[]{SubmissionStatus.VERIFIED, SubmissionStatus.ACCEPTED};
        Submission savedSubmission1 = EntityDTOUtils.prepareSubmission("test-title1", SubmissionStatus.VERIFIED);
        Submission expectedSubmission = EntityDTOUtils.prepareSubmission(searchedTitle, SubmissionStatus.ACCEPTED);
        Submission savedSubmission3 = EntityDTOUtils.prepareSubmission("test-title3", SubmissionStatus.VERIFIED);
        submissionRepository.save(savedSubmission1);
        submissionRepository.save(expectedSubmission);
        submissionRepository.save(savedSubmission3);

        Optional<Submission> foundOptional = submissionRepository.findByTitleAndStatusIn(searchedTitle, Arrays.asList(requiredStatuses));
        foundOptional.ifPresentOrElse(actualSubmission -> {
            assertTrue(EntityDTOUtils.compareSubmissionsWithoutIdAndDates(expectedSubmission, actualSubmission));
        }, () -> {
            throw new IllegalStateException("Entity not saved correctly!");
        });
    }

}