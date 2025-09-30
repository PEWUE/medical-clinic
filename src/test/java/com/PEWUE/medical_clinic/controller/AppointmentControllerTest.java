package com.PEWUE.medical_clinic.controller;

import com.PEWUE.medical_clinic.command.AppointmentCreateCommand;
import com.PEWUE.medical_clinic.command.BookAppointmentCommand;
import com.PEWUE.medical_clinic.dto.AppointmentDto;
import com.PEWUE.medical_clinic.dto.PageDto;
import com.PEWUE.medical_clinic.mapper.AppointmentMapper;
import com.PEWUE.medical_clinic.mapper.PageMapper;
import com.PEWUE.medical_clinic.model.Appointment;
import com.PEWUE.medical_clinic.model.Doctor;
import com.PEWUE.medical_clinic.model.Patient;
import com.PEWUE.medical_clinic.service.AppointmentService;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;

import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AppointmentControllerTest {
    @MockitoBean
    AppointmentService appointmentService;
    @MockitoBean
    AppointmentMapper appointmentMapper;
    @MockitoBean
    PageMapper pageMapper;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    MockMvc mockMvc;

    @Test
    void shouldReturnPagedAppointmentDtosWhenDataCorrect() throws Exception {
        List<Appointment> appointments = List.of(
                Appointment.builder().id(1L).build(),
                Appointment.builder().id(2L).build()
        );
        List<AppointmentDto> appointmentDtos = List.of(
                AppointmentDto.builder().id(1L).doctorId(1L).patientId(2L).build(),
                AppointmentDto.builder().id(2L).doctorId(1L).patientId(2L).build()
        );
        Long doctorId = 1L;
        Long patientId = 2L;
        Pageable pageable = PageRequest.of(0, 2);
        Page<Appointment> page = new PageImpl<>(appointments, pageable, appointments.size());
        PageDto<AppointmentDto> pageDto = new PageDto<>(
                appointmentDtos,
                0,
                2,
                2,
                1
        );

        when(appointmentService.find(doctorId, patientId, pageable)).thenReturn(page);
        when(pageMapper.toDto(any(Page.class), any(Function.class))).thenReturn(pageDto);

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/appointments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("doctorId", doctorId.toString())
                                .param("patientId", patientId.toString())
                                .param("page", "0")
                                .param("size", "2"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.pageSize").value(2),
                        jsonPath("$.pageNumber").value(0),
                        jsonPath("$.content").isArray(),
                        jsonPath("$.content[0].id").value(1),
                        jsonPath("$.content[0].doctorId").value(1),
                        jsonPath("$.content[0].patientId").value(2),
                        jsonPath("$.content[1].id").value(2),
                        jsonPath("$.content[1].doctorId").value(1),
                        jsonPath("$.content[1].patientId").value(2)
                );
    }

    @Test
    void shouldReturnCreatedAppointmentDtoWhenValidCreateCommandProvided() throws Exception {
        AppointmentCreateCommand command = AppointmentCreateCommand.builder()
                .doctorId(5L)
                .startTime(LocalDateTime.of(2026, 5, 5, 15, 15))
                .endTime(LocalDateTime.of(2026, 5, 5, 15, 45))
                .build();
        Appointment appointment = Appointment.builder()
                .id(9L)
                .doctor(Doctor.builder().id(5L).build())
                .patient(null)
                .startTime(LocalDateTime.of(2026, 5, 5, 15, 15))
                .endTime(LocalDateTime.of(2026, 5, 5, 15, 45))
                .build();
        AppointmentDto appointmentDto = AppointmentDto.builder()
                .id(9L)
                .doctorId(5L)
                .patientId(null)
                .startTime(LocalDateTime.of(2026, 5, 5, 15, 15))
                .endTime(LocalDateTime.of(2026, 5, 5, 15, 45))
                .build();

        when(appointmentService.add(command)).thenReturn(appointment);
        when(appointmentMapper.toDto(appointment)).thenReturn(appointmentDto);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command))
        ).andExpectAll(
                status().isCreated(),
                jsonPath("$.id").value(9),
                jsonPath("$.doctorId").value(5),
                jsonPath("$.patientId").value(nullValue()),
                jsonPath("$.startTime").value("2026-05-05T15:15:00"),
                jsonPath("$.endTime").value("2026-05-05T15:45:00")
        );
    }

    @Test
    void shouldReturnAppointmentDtoWhenBookingIsSuccessful() throws Exception {
        BookAppointmentCommand command = BookAppointmentCommand.builder()
                .appointmentId(2L)
                .patientId(5L)
                .build();
        Appointment appointment = Appointment.builder()
                .id(2L)
                .doctor(Doctor.builder().id(4L).build())
                .patient(Patient.builder().id(5L).build())
                .startTime(LocalDateTime.of(2026, 5, 10, 8, 30))
                .endTime(LocalDateTime.of(2026, 5, 10, 9, 0))
                .build();
        AppointmentDto appointmentDto = AppointmentDto.builder()
                .id(2L)
                .doctorId(4L)
                .patientId(5L)
                .startTime(LocalDateTime.of(2026, 5, 10, 8, 30))
                .endTime(LocalDateTime.of(2026, 5, 10, 9, 0))
                .build();

        when(appointmentService.book(command)).thenReturn(appointment);
        when(appointmentMapper.toDto(appointment)).thenReturn(appointmentDto);

        mockMvc.perform(
                MockMvcRequestBuilders.patch("/appointments/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command))
        ).andExpectAll(
                jsonPath("$.id").value(2),
                jsonPath("$.doctorId").value(4),
                jsonPath("$.patientId").value(5),
                jsonPath("$.startTime").value("2026-05-10T08:30:00"),
                jsonPath("$.endTime").value("2026-05-10T09:00:00")
        );
    }
}