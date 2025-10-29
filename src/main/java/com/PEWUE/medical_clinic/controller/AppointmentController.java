package com.PEWUE.medical_clinic.controller;

import com.PEWUE.medical_clinic.command.AppointmentCreateCommand;
import com.PEWUE.medical_clinic.command.BookAppointmentCommand;
import com.PEWUE.medical_clinic.dto.AppointmentDto;
import com.PEWUE.medical_clinic.dto.ErrorMessageDto;
import com.PEWUE.medical_clinic.dto.PageDto;
import com.PEWUE.medical_clinic.mapper.AppointmentMapper;
import com.PEWUE.medical_clinic.mapper.PageMapper;
import com.PEWUE.medical_clinic.model.Appointment;
import com.PEWUE.medical_clinic.service.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/appointments")
@Slf4j
@Tag(name = "Appointments operations")
public class AppointmentController {
    private final AppointmentService appointmentService;
    private final AppointmentMapper appointmentMapper;
    private final PageMapper pageMapper;

    @Operation(summary = "Get appointments list")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "List of appointments returned",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = AppointmentDto.class)))),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessageDto.class)))})
    @GetMapping
    public PageDto<AppointmentDto> find(@RequestParam(required = false) Long doctorId,
                                        @RequestParam(required = false) Long patientId,
                                        @ParameterObject Pageable pageable) {
        log.info("Received GET /appointments, doctorId={}, patientId={}, pageable={}", doctorId, patientId, pageable);
        Page<Appointment> page = appointmentService.find(doctorId, patientId, pageable);
        return pageMapper.toDto(page, appointmentMapper::toDto);
    }

    @Operation(summary = "Get appointment details by appointment ID")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Appointment details returned successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppointmentDto.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Appointment with the specified ID not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    @GetMapping("/{appointmentId}")
    public AppointmentDto getAppointmentById(@PathVariable Long appointmentId) {
        log.info("Received GET /appointments/{}", appointmentId);
        Appointment appointment = appointmentService.findById(appointmentId);
        return appointmentMapper.toDto(appointment);
    }


    @Operation(summary = "Get free appointment slots list filtered by doctor ID")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "List of free appointment slots returned",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = AppointmentDto.class)))),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessageDto.class)))})
    @GetMapping("/free-slots/doctor")
    public PageDto<AppointmentDto> findFreeSlots(@RequestParam(required = false) Long doctorId,
                                                 @ParameterObject Pageable pageable) {
        log.info("Received GET /appointments/free-slots, doctorId={}, pageable={}", doctorId, pageable);
        Page<Appointment> page = appointmentService.findFreeSlots(doctorId, pageable);
        return pageMapper.toDto(page, appointmentMapper::toDto);
    }

    @Operation(summary = "Get free appointment slots list by specialization and date")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "List of free appointment slots returned",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = AppointmentDto.class)))),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessageDto.class)))})
    @GetMapping("/free-slots/specialization")
    public PageDto<AppointmentDto> findFreeSlots(
            @RequestParam String specialization,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @ParameterObject Pageable pageable) {
        log.info("Received GET /appointments/free-slots, specialization={}, date={}, pageable={}", specialization, date, pageable);
        Page<Appointment> page = appointmentService.findFreeAppointmentsBySpecializationAndDay(specialization, date, pageable);
        return pageMapper.toDto(page, appointmentMapper::toDto);
    }

    @Operation(summary = "Find appointments by patient, specialization and date range")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Paginated list of appointment slots returned",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = AppointmentDto.class)))),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid time range or missing data",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    @GetMapping("/appointments/patient/{patientId}/specialization-range")
    public PageDto<AppointmentDto> findPatientAppointmentsBySpecializationAndRange(
            @PathVariable Long patientId,
            @RequestParam(required = false) String specialization,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @ParameterObject Pageable pageable
    ) {
        Page<Appointment> page = appointmentService.findPatientAppointmentsBySpecializationAndTimeRange(
                patientId, specialization, from, to, pageable
        );
        return pageMapper.toDto(page, appointmentMapper::toDto);
    }



    @Operation(summary = "Create a new available appointment slot for a doctor")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Appointment slot successfully created",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AppointmentDto.class))),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data (e.g., overlapping appointment or invalid times)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Doctor not found for the given doctorId",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessageDto.class)))})
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AppointmentDto add(@RequestBody AppointmentCreateCommand appointmentCreateCommand) {
        log.info("Received POST /appointments to create slot for doctorId={}", appointmentCreateCommand.doctorId());
        return appointmentMapper.toDto(appointmentService.add(appointmentCreateCommand));
    }

    @Operation(summary = "Book an available appointment slot by assigning a patient")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Appointment successfully booked",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AppointmentDto.class))),
            @ApiResponse(
                    responseCode = "400",
                    description = "Appointment already booked or invalid patient ID",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Appointment or patient not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessageDto.class)))})
    @PatchMapping("/book")
    public AppointmentDto book(@RequestBody BookAppointmentCommand command) {
        log.info("Received PATCH /appointments/book to book appointmentId={} for patientId={}", command.appointmentId(), command.patientId());
        return appointmentMapper.toDto(appointmentService.book(command));
    }

    @Operation(summary = "Cancel (delete) appointment")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Appointment successfully canceled (deleted)"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Appointment not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    @DeleteMapping("/{appointmentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelAppointment(@PathVariable Long appointmentId) {
        appointmentService.cancel(appointmentId);
    }
}