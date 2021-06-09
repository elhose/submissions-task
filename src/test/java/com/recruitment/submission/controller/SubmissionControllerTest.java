package com.recruitment.submission.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recruitment.submission.dto.PresentationDTO;
import com.recruitment.submission.dto.RejectionDTO;
import com.recruitment.submission.dto.SubmissionDTO;
import com.recruitment.submission.dto.TitleDTO;
import com.recruitment.submission.entity.SubmissionStatus;
import com.recruitment.submission.initializer.Postgres;
import com.recruitment.submission.service.SubmissionService;
import com.recruitment.submission.utils.EntityDTOUtils;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = SubmissionController.class)
@ContextConfiguration(initializers = {Postgres.Initializer.class})
class SubmissionControllerTest {

    @MockBean
    private SubmissionService submissionService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreate() throws Exception {
        SubmissionDTO submissionDTO = EntityDTOUtils.prepareSubmissionDTO();

        Mockito.doNothing().when(submissionService).createSubmission(submissionDTO);

        mockMvc.perform(post("/api/create")
                                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(submissionDTO))
                       ).andExpect(status().isCreated());
    }

    @Test
    void testVerify() throws Exception {
        SubmissionDTO submissionDTO = EntityDTOUtils.prepareSubmissionDTO();

        Mockito.doNothing().when(submissionService).verifySubmission(submissionDTO);

        mockMvc.perform(post("/api/verify")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(submissionDTO))
                       ).andExpect(status().isAccepted());
    }

    @Test
    void testDelete() throws Exception {
        RejectionDTO rejectionDTO = EntityDTOUtils.prepareRejectionDTO();

        Mockito.doNothing().when(submissionService).deleteSubmission(rejectionDTO);

        mockMvc.perform(delete("/api/delete")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(rejectionDTO))
                       ).andExpect(status().isNoContent());
    }

    @Test
    void testReject() throws Exception {
        RejectionDTO rejectionDTO = EntityDTOUtils.prepareRejectionDTO();

        Mockito.doNothing().when(submissionService).rejectSubmission(rejectionDTO);

        mockMvc.perform(delete("/api/delete")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(rejectionDTO))
                       ).andExpect(status().isNoContent());
    }

    @Test
    void testAccept() throws Exception {
        TitleDTO titleDTO = EntityDTOUtils.prepareTitleDTO();

        Mockito.doNothing().when(submissionService).acceptSubmission(titleDTO.getTitle());

        mockMvc.perform(post("/api/accept")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(titleDTO))
                       ).andExpect(status().isAccepted());
    }

    @Test
    void testPublish() throws Exception {
        TitleDTO titleDTO = EntityDTOUtils.prepareTitleDTO();

        Mockito.doNothing().when(submissionService).publishSubmission(titleDTO.getTitle());

        mockMvc.perform(post("/api/publish")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(titleDTO))
                       ).andExpect(status().isAccepted());

    }

    @Test
    void testCurrentNoParametersProvided() throws Exception {
        PresentationDTO submissionDTO1 = EntityDTOUtils.preparePresentationDTO("title1", SubmissionStatus.DELETED);
        PresentationDTO submissionDTO2 = EntityDTOUtils.preparePresentationDTO("title2", SubmissionStatus.ACCEPTED);

        Mockito.when(submissionService.listActualSubmissions(null, null)).thenReturn(Arrays.asList(submissionDTO1, submissionDTO2));

        mockMvc.perform(get("/api/current")
                                .contentType(MediaType.APPLICATION_JSON)
                       ).andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$.content.size()", Matchers.is(2)))
               .andExpect(jsonPath("$.content.[0].title", Matchers.is("title1")))
               .andExpect(jsonPath("$.content.[1].title", Matchers.is("title2")))
               .andExpect(jsonPath("$.content.[0].status", Matchers.is(SubmissionStatus.DELETED.toString())))
               .andExpect(jsonPath("$.content.[1].status", Matchers.is(SubmissionStatus.ACCEPTED.toString())));
    }

    @Test
    void testCurrentTitleParameterProvided() throws Exception {
        String foundTitle = "title1";
        PresentationDTO foundSubmission = EntityDTOUtils.preparePresentationDTO(foundTitle, SubmissionStatus.DELETED);

        Mockito.when(submissionService.listActualSubmissions(foundTitle, null)).thenReturn(Collections.singletonList(foundSubmission));

        mockMvc.perform(get("/api/current")
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("title", foundTitle)
                       ).andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$.content.size()", Matchers.is(1)))
               .andExpect(jsonPath("$.content.[0].title", Matchers.is(foundTitle)))
               .andExpect(jsonPath("$.content.[0].status", Matchers.is(SubmissionStatus.DELETED.toString())));
    }

    @Test
    void testCurrentStatusParameterProvided() throws Exception {
        SubmissionStatus foundStatus = SubmissionStatus.DELETED;
        PresentationDTO foundSubmission = EntityDTOUtils.preparePresentationDTO("title1", foundStatus);

        Mockito.when(submissionService.listActualSubmissions(null, foundStatus)).thenReturn(Collections.singletonList(foundSubmission));

        mockMvc.perform(get("/api/current")
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("status", foundStatus.toString())
                       ).andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$.content.size()", Matchers.is(1)))
               .andExpect(jsonPath("$.content.[0].title", Matchers.is("title1")))
               .andExpect(jsonPath("$.content.[0].status", Matchers.is(foundStatus.toString())));
    }

    @Test
    void testCurrentStatusAndTitleParametersProvided() throws Exception {
        SubmissionStatus foundStatus = SubmissionStatus.DELETED;
        String foundTitle = "title1";
        PresentationDTO foundSubmission = EntityDTOUtils.preparePresentationDTO(foundTitle, foundStatus);

        Mockito.when(submissionService.listActualSubmissions(foundTitle, foundStatus)).thenReturn(Collections.singletonList(foundSubmission));

        mockMvc.perform(get("/api/current")
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("title", foundTitle)
                                .param("status", foundStatus.toString())
                       ).andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$.content.size()", Matchers.is(1)))
               .andExpect(jsonPath("$.content.[0].title", Matchers.is(foundTitle)))
               .andExpect(jsonPath("$.content.[0].status", Matchers.is(foundStatus.toString())));
    }

    @Test
    void testHistory() throws Exception {
        TitleDTO titleDTO = EntityDTOUtils.prepareTitleDTO();
        String title = titleDTO.getTitle();
        PresentationDTO presentationDTO1 = EntityDTOUtils.preparePresentationDTO(title, SubmissionStatus.CREATED);
        PresentationDTO presentationDTO2 = EntityDTOUtils.preparePresentationDTO(title, SubmissionStatus.VERIFIED);
        PresentationDTO presentationDTO3 = EntityDTOUtils.preparePresentationDTO(title, SubmissionStatus.DELETED);
        Mockito.when(submissionService.getHistory(title)).thenReturn(new ArrayList<>(Arrays.asList(presentationDTO3, presentationDTO2, presentationDTO1)));

        mockMvc.perform(get("/api/history")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(titleDTO))
                       ).andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$.size()", Matchers.is(3)))
               .andExpect(jsonPath("$.[0].title", Matchers.is(title)))
               .andExpect(jsonPath("$.[0].status", Matchers.is(presentationDTO3.getStatus().toString())))
               .andExpect(jsonPath("$.[1].title", Matchers.is(title)))
               .andExpect(jsonPath("$.[1].status", Matchers.is(presentationDTO2.getStatus().toString())))
               .andExpect(jsonPath("$.[2].title", Matchers.is(title)))
               .andExpect(jsonPath("$.[2].status", Matchers.is(presentationDTO1.getStatus().toString())));
    }
}