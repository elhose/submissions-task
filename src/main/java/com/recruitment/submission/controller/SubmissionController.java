package com.recruitment.submission.controller;

import com.recruitment.submission.dto.PresentationDTO;
import com.recruitment.submission.dto.RejectionDTO;
import com.recruitment.submission.dto.SubmissionDTO;
import com.recruitment.submission.dto.TitleDTO;
import com.recruitment.submission.entity.SubmissionStatus;
import com.recruitment.submission.service.SubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PostMapping("publish")
    public ResponseEntity<HttpStatus> publish(@RequestBody TitleDTO titleDTO) {
        submissionService.publishSubmission(titleDTO.getTitle());
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @GetMapping("current")
    public ResponseEntity<Page<PresentationDTO>> list(@RequestParam(required = false) String title, @RequestParam(required = false) SubmissionStatus status) {
        List<PresentationDTO> filteredDtos = submissionService.listActualSubmissions(title, status);
        return new ResponseEntity<>(new PageImpl<>(filteredDtos), HttpStatus.OK);
    }

    @GetMapping("history")
    public ResponseEntity<List<PresentationDTO>> getSubmissionHistory(@RequestBody TitleDTO submissionTitle) {
        List<PresentationDTO> history = submissionService.getHistory(submissionTitle.getTitle());
        return new ResponseEntity<>(history, HttpStatus.OK);
    }

}
