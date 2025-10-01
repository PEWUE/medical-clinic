package com.PEWUE.medical_clinic.controller;

import com.PEWUE.medical_clinic.command.PatientCreateCommand;
import com.PEWUE.medical_clinic.command.PatientEditCommand;
import com.PEWUE.medical_clinic.command.UserCreateCommand;
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

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
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

    @Test
    void shouldReturnCreatedPatientDtoWhenCorrectDataProvided() throws Exception {
        PatientCreateCommand command = PatientCreateCommand.builder()
                .firstName("Patient1")
                .lastName("Last1")
                .idCardNo("ABC-123")
                .phoneNumber("999-888-777")
                .birthday(LocalDate.of(1995, 11, 14))
                .user(UserCreateCommand.builder().email("email@patient.com").username("username").password("pass123").build())
                .build();
        Patient patient = Patient.builder()
                .id(13L)
                .firstName("Patient1")
                .lastName("Last1")
                .idCardNo("ABC-123")
                .phoneNumber("999-888-777")
                .birthday(LocalDate.of(1995, 11, 14))
                .user(User.builder().id(21L).email("email@patient.com").username("username").password("pass123").build())
                .build();

        when(patientService.add(any(Patient.class))).thenReturn(patient);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command))
        ).andExpectAll(
                status().isCreated(),
                jsonPath("$.id").value(13),
                jsonPath("$.user.id").value(21),
                jsonPath("$.user.email").value("email@patient.com"),
                jsonPath("$.user.username").value("username"),
                jsonPath("$.user.password").doesNotExist()
        );
    }

    @Test
    void shouldDeletePatientWhenValidEmailProvided() throws Exception {
        String email = "patient@example.com";

        mockMvc.perform(
                MockMvcRequestBuilders.delete("/patients/{email}", email)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                status().isNoContent()
        );

        verify(patientService).delete(email);
    }

    @Test
    void shouldReturnUpdatedPatientDtoWhenValidEmailAndEditCommandProvided() throws Exception {
        String email = "patient@website.com";
        PatientEditCommand command = PatientEditCommand.builder()
                .firstName("updatedName")
                .lastName("updatedLastname")
                .phoneNumber("111-222-333")
                .birthday(LocalDate.of(1990, 6, 1))
                .build();
        Patient patient = Patient.builder()
                .id(12L)
                .firstName("updatedName")
                .lastName("updatedLastname")
                .phoneNumber("111-222-333")
                .birthday(LocalDate.of(1990, 6, 1))
                .build();

        when(patientService.edit(eq(email), any(Patient.class))).thenReturn(patient);

        mockMvc.perform(
                MockMvcRequestBuilders.put("/patients/{email}", email)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command))
        ).andExpectAll(
                status().isOk(),
                jsonPath("$.id").value(12),
                jsonPath("$.firstName").value("updatedName"),
                jsonPath("$.lastName").value("updatedLastname"),
                jsonPath("$.phoneNumber").value("111-222-333"),
                jsonPath("$.birthday").value("1990-06-01")
        );
    }
}
