package com.recruitment.submission.utils;

import com.recruitment.submission.dto.SubmissionDTO;
import com.recruitment.submission.entity.HistorySubmission;
import com.recruitment.submission.entity.Submission;
import com.recruitment.submission.entity.SubmissionStatus;

import java.time.LocalDateTime;
import java.util.*;

public class EntityDTOUtils {
    public static SubmissionDTO prepareSubmissionDTO() {
        SubmissionDTO submissionDTO = new SubmissionDTO();
        submissionDTO.setTitle("title-submissionDTO");
        submissionDTO.setContent("content-submissionDTO");
        return submissionDTO;
    }

    public static Submission prepareSubmission() {
        Set<HistorySubmission> historySubmissions = new LinkedHashSet<>(Arrays.asList(prepareHistorySubmission(), prepareHistorySubmission(), prepareHistorySubmission()));
        Submission submission = new Submission();
        submission.setId(generateLong());
        submission.setTitle("title-submission");
        submission.setContent("content-submission");
        submission.setStatus(SubmissionStatus.CREATED);
        submission.setReason("reason-submission");
        submission.setPublicId(generateLong());
        submission.setHistory(historySubmissions);
        submission.setCreateDateTime(LocalDateTime.now().minusDays(1));
        submission.setUpdateDateTime(LocalDateTime.now().plusDays(1));
        return submission;
    }

    public static Submission prepareSubmission(String title) {
        Submission submission = prepareSubmission();
        submission.setTitle(title);
        return submission;
    }

    public static Submission prepareSubmission(String title, SubmissionStatus submissionStatus) {
        Submission submission = prepareSubmission();
        submission.setTitle(title);
        submission.setStatus(submissionStatus);
        return submission;
    }

    public static HistorySubmission prepareHistorySubmission() {
        HistorySubmission historySubmission = new HistorySubmission();
        historySubmission.setId(generateLong());
        historySubmission.setTitle("title-historySubmission");
        historySubmission.setContent("content-historySubmission");
        historySubmission.setStatus(SubmissionStatus.ACCEPTED);
        historySubmission.setCreateDateTime(LocalDateTime.now().minusHours(1));
        historySubmission.setUpdateDateTime(LocalDateTime.now().plusHours(1));
        return historySubmission;
    }

    private static Long generateLong() {
        return new Random().nextLong();
    }


    // Comparisons without ID's and Dates are used when testing DB, DB handles id's and dates
    public static boolean compareSubmissionsWithoutIdAndDates(Submission first, Submission second) {
        return first.getTitle().equals(second.getTitle()) &&
                first.getContent().equals(second.getContent()) &&
                first.getStatus().equals(second.getStatus()) &&
                first.getReason().equals(second.getReason()) &&
                first.getPublicId().equals(second.getPublicId()) &&
                compareHistorySetsWithoutIdAndDates(first.getHistory(), second.getHistory());
    }

    private static boolean compareHistorySetsWithoutIdAndDates(Set<HistorySubmission> first, Set<HistorySubmission> second) {
        if (first.size() != second.size()) {
            return false;
        }
        Iterator<HistorySubmission> firstIterator = first.iterator();
        Iterator<HistorySubmission> secondIterator = second.iterator();
        while (firstIterator.hasNext()) {
            if (!compareHistorySubmissionWithoutIdAndDates(firstIterator.next(), secondIterator.next())) {
                return false;
            }
        }
        return true;
    }

    private static boolean compareHistorySubmissionWithoutIdAndDates(HistorySubmission first, HistorySubmission second) {
        return first.getTitle().equals(second.getTitle()) &&
                first.getContent().equals(second.getContent()) &&
                first.getStatus().equals(second.getStatus());
    }

}
