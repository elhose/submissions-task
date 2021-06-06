package com.recruitment.submission.controller;

import com.recruitment.submission.dto.SubmissionDTO;
import com.recruitment.submission.service.SubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api")
public class SubmissionController {

    private final SubmissionService submissionService;

    @PostMapping("create")
    public ResponseEntity<HttpStatus> create(@RequestBody SubmissionDTO createdSubmission) {
        submissionService.createSubmission(createdSubmission);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

}
