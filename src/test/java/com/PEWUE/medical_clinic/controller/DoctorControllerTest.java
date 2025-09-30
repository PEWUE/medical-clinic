package com.PEWUE.medical_clinic.controller;

import com.PEWUE.medical_clinic.command.DoctorCreateCommand;
import com.PEWUE.medical_clinic.command.UserCreateCommand;
import com.PEWUE.medical_clinic.dto.DoctorDto;
import com.PEWUE.medical_clinic.dto.PageDto;
import com.PEWUE.medical_clinic.dto.UserDto;
import com.PEWUE.medical_clinic.mapper.DoctorMapper;
import com.PEWUE.medical_clinic.mapper.PageMapper;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static org.hamcrest.Matchers.hasItems;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class DoctorControllerTest {
    @MockitoBean
    DoctorService doctorService;
    @MockitoBean
    DoctorMapper doctorMapper;
    @MockitoBean
    PageMapper pageMapper;
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
        List<DoctorDto> doctorDtos = List.of(
                DoctorDto.builder().id(1L).firstName("name1").lastName("lastname1").specialization("specialization1").institutionsIds(List.of(1L, 3L, 5L)).build(),
                DoctorDto.builder().id(2L).firstName("name2").lastName("lastname2").specialization("specialization2").institutionsIds(List.of(2L, 4L, 6L)).build()
        );
        Pageable pageable = PageRequest.of(0, 2);
        Page<Doctor> page = new PageImpl<>(doctors, pageable, doctors.size());
        PageDto<DoctorDto> pageDto = new PageDto<>(
                doctorDtos,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );

        when(doctorService.find(pageable)).thenReturn(page);
        when(pageMapper.toDto(any(Page.class), any(Function.class))).thenReturn(pageDto);

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
                jsonPath("$.content[0].institutionsIds[*]", hasItems(1, 3, 5)),
                jsonPath("$.content[1].firstName").value("name2"),
                jsonPath("$.content[1].lastName").value("lastname2"),
                jsonPath("$.content[1].specialization").value("specialization2"),
                jsonPath("$.content[1].institutionsIds[*]", hasItems(2, 4, 6))
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
                .firstName("name1")
                .lastName("lastname1")
                .specialization("specialization1")
                .user(User.builder().email("email@doctor.com").username("username1").password("pa$$word1").build())
                .build();
        DoctorDto doctorDto = DoctorDto.builder()
                .id(66L)
                .firstName("name1")
                .lastName("lastname1")
                .specialization("specialization1")
                .user(UserDto.builder().id(98L).email("email@doctor.com").username("username1").build())
                .institutionsIds(new ArrayList<>())
                .appointmentsIds(new ArrayList<>())
                .build();

        when(doctorMapper.toEntity(command)).thenReturn(doctor);
        when(doctorService.add(doctor)).thenReturn(doctor);
        when(doctorMapper.toDto(doctor)).thenReturn(doctorDto);

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
}
