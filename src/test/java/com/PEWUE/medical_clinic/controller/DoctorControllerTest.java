package com.PEWUE.medical_clinic.controller;

import com.PEWUE.medical_clinic.command.DoctorCreateCommand;
import com.PEWUE.medical_clinic.command.DoctorEditCommand;
import com.PEWUE.medical_clinic.command.UserCreateCommand;
import com.PEWUE.medical_clinic.model.Doctor;
import com.PEWUE.medical_clinic.model.User;
import com.PEWUE.medical_clinic.service.DoctorService;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class DoctorControllerTest {
    @MockitoBean
    DoctorService doctorService;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    MockMvc mockMvc;

    @Test
    void shouldReturnPagedDoctorDtosWhenDataCorrect() throws Exception {
        List<Doctor> doctors = List.of(
                Doctor.builder().id(1L).firstName("name1").lastName("lastname1").specialization("specialization1").build(),
                Doctor.builder().id(2L).firstName("name2").lastName("lastname2").specialization("specialization2").build()
        );
        Pageable pageable = PageRequest.of(0, 2);
        Page<Doctor> page = new PageImpl<>(doctors, pageable, doctors.size());

        when(doctorService.find(pageable)).thenReturn(page);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/doctors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("page", "0")
                        .param("size", "2")
        ).andExpectAll(
                status().isOk(),
                jsonPath("$.content").isArray(),
                jsonPath("$.content[0].firstName").value("name1"),
                jsonPath("$.content[0].lastName").value("lastname1"),
                jsonPath("$.content[0].specialization").value("specialization1"),
                jsonPath("$.content[0].institutionsIds").isArray(),
                jsonPath("$.content[1].firstName").value("name2"),
                jsonPath("$.content[1].lastName").value("lastname2"),
                jsonPath("$.content[1].specialization").value("specialization2")
        );
    }

    @Test
    void shouldReturnCreatedDoctorDtoWhenValidCreateCommandProvided() throws Exception {
        DoctorCreateCommand command = DoctorCreateCommand.builder()
                .firstName("name1")
                .lastName("lastname1")
                .specialization("specialization1")
                .user(UserCreateCommand.builder().email("email@doctor.com").username("username1").password("pa$$word1").build())
                .build();
        Doctor doctor = Doctor.builder()
                .id(66L)
                .firstName("name1")
                .lastName("lastname1")
                .specialization("specialization1")
                .user(User.builder().id(98L).email("email@doctor.com").username("username1").password("pa$$word1").build())
                .build();

        when(doctorService.add(any(Doctor.class))).thenReturn(doctor);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/doctors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command))
        ).andExpectAll(
                status().isCreated(),
                jsonPath("$.id").value(66),
                jsonPath("$.firstName").value("name1"),
                jsonPath("$.lastName").value("lastname1"),
                jsonPath("$.specialization").value("specialization1"),
                jsonPath("$.user.id").value(98),
                jsonPath("$.user.email").value("email@doctor.com"),
                jsonPath("$.user.username").value("username1"),
                jsonPath("$.institutionsIds").isEmpty(),
                jsonPath("$.appointmentsIds").isEmpty()
        );
    }

    @Test
    void shouldDeleteDoctorWhenValidEmailProvided() throws Exception {
        String email = "doctor@clinic.com";

        mockMvc.perform(
                MockMvcRequestBuilders.delete("/doctors/{email}", email)
        ).andExpect(status().isNoContent());

        verify(doctorService).delete(email);
    }

    @Test
    void shouldReturnUpdatedDoctorDtoWhenValidEmailAndEditCommandProvided() throws Exception {
        String email = "doctor@clinic.com";
        DoctorEditCommand command = DoctorEditCommand.builder()
                .firstName("updatedName")
                .lastName("updatedLastname")
                .specialization("updatedSpecialization")
                .build();
        Doctor updatedDoctor = Doctor.builder()
                .id(56L)
                .firstName("updatedName")
                .lastName("updatedLastname")
                .specialization("updatedSpecialization")
                .build();

        when(doctorService.edit(eq(email), any(Doctor.class))).thenReturn(updatedDoctor);

        mockMvc.perform(
                MockMvcRequestBuilders.put("/doctors/{email}", email)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command))
        ).andExpectAll(
                status().isOk(),
                jsonPath("$.id").value(56),
                jsonPath("$.firstName").value("updatedName"),
                jsonPath("$.lastName").value("updatedLastname"),
                jsonPath("$.specialization").value("updatedSpecialization")
        );
    }
}
