package com.PEWUE.medical_clinic.controller;

import com.PEWUE.medical_clinic.model.Patient;
import com.PEWUE.medical_clinic.model.User;
import com.PEWUE.medical_clinic.service.PatientService;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PatientControllerTest {
    @MockitoBean
    PatientService patientService;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    MockMvc mockMvc;

    @Test
    void shouldReturnPagedPatientDtosWhenDataCorrect() throws Exception {
        List<Patient> patients = List.of(
                Patient.builder().id(1L).build(),
                Patient.builder().id(2L).build()
        );
        Pageable pageable = PageRequest.of(0, 2);
        Page<Patient> page = new PageImpl<>(patients, pageable, patients.size());

        when(patientService.find(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/patients")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isOk(),
                jsonPath("$.content[0].id").value(1),
                jsonPath("$.content[1].id").value(2)
        );
    }

    @Test
    void shouldReturnPatientDtoWhenValidEmailProvided() throws Exception {
        String email = "given@email.com";
        Patient patient = Patient.builder()
                .id(32L)
                .user(User.builder().email("given@email.com").build())
                .build();

        when(patientService.find(email)).thenReturn(patient);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/patients/{email}", email)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isOk(),
                jsonPath("$.id").value(32),
                jsonPath("$.user.email").value("given@email.com")
                );
    }
}
