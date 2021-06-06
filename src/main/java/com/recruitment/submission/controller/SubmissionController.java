package com.recruitment.submission.controller;

import com.recruitment.submission.dto.RejectionDTO;
import com.recruitment.submission.dto.SubmissionDTO;
import com.recruitment.submission.dto.TitleDTO;
import com.recruitment.submission.service.SubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("verify")
    public ResponseEntity<HttpStatus> verify(@RequestBody SubmissionDTO verifiedSubmission) {
        submissionService.verifySubmission(verifiedSubmission);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @DeleteMapping("delete")
    public ResponseEntity<HttpStatus> delete(@RequestBody RejectionDTO rejectionDTO) {
        submissionService.deleteSubmission(rejectionDTO);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("reject")
    public ResponseEntity<HttpStatus> reject(@RequestBody RejectionDTO rejectionDTO) {
        submissionService.rejectSubmission(rejectionDTO);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("accept")
    public ResponseEntity<HttpStatus> accept(@RequestBody TitleDTO titleDTO) {
        submissionService.acceptSubmission(titleDTO.getTitle());
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

}
