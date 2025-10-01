package com.PEWUE.medical_clinic.controller;

import com.PEWUE.medical_clinic.command.InstitutionCreateCommand;
import com.PEWUE.medical_clinic.model.Doctor;
import com.PEWUE.medical_clinic.model.Institution;
import com.PEWUE.medical_clinic.service.InstitutionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class InstitutionControllerTest {
    @MockitoBean
    InstitutionService institutionService;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    MockMvc mockMvc;

    @Test
    void shouldReturnPagedInstitutionDtosWhenDataCorrect() throws Exception {
        List<Institution> institutions = List.of(
                Institution.builder().id(1L).build(),
                Institution.builder().id(2L).build()
        );
        Pageable pageable = PageRequest.of(0, 2);
        Page<Institution> page = new PageImpl<>(institutions, pageable, institutions.size());

        when(institutionService.find(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/institutions")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isOk(),
                jsonPath("$.content[0].id").value(1),
                jsonPath("$.content[1].id").value(2)
        );
    }

    @Test
    void shouldReturnCreatedInstitutionDtoWhenValidCreateCommandProvided() throws Exception {
        InstitutionCreateCommand command = InstitutionCreateCommand.builder()
                .name("Institution name 1")
                .city("City 1")
                .postalCode("POSTAL-CODE")
                .street("Street One")
                .streetNo("242a")
                .build();
        Institution institution = Institution.builder()
                .id(11L)
                .name("Institution name 1")
                .city("City 1")
                .postalCode("POSTAL-CODE")
                .street("Street One")
                .streetNo("242a")
                .build();

        when(institutionService.add(any(Institution.class))).thenReturn(institution);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/institutions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command))
        ).andExpectAll(
                status().isCreated(),
                jsonPath("$.id").value(11),
                jsonPath("$.doctorsIds").isEmpty()
        );
    }

    @Test
    void shouldDeleteInstitutionWhenValidIdProvided() throws Exception {
        Long institutionId = 432L;

        mockMvc.perform(
                MockMvcRequestBuilders.delete("/institutions/{id}", institutionId)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent());

        verify(institutionService).delete(institutionId);
    }

    @Test
    void shouldReturnInstitutionDtoWithAssignedDoctorIdWhenValidIdsProvided() throws Exception {
        Long doctorId = 32L;
        Long institutionId = 22L;
        Institution institution = Institution.builder()
                .id(22L)
                .name("Institution name 1")
                .doctors(List.of(Doctor.builder().id(32L).build()))
                .build();

        when(institutionService.assignDoctorToInstitution(doctorId, institutionId)).thenReturn(institution);

        mockMvc.perform(
                MockMvcRequestBuilders.put("/institutions/{institutionId}/doctors/{doctorId}", institutionId, doctorId)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isOk(),
                jsonPath("$.id").value(22),
                jsonPath("$.doctorsIds[0]").value(32)
        );
    }
}
