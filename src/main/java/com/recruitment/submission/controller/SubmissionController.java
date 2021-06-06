package com.recruitment.submission.controller;

import com.recruitment.submission.entity.Submission;
import com.recruitment.submission.service.SubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api")
public class SubmissionController {

    private final SubmissionService submissionService;

    @GetMapping("list")
    public List<Submission> testController() {
        return submissionService.getAll();
    }

}
