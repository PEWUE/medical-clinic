package com.PEWUE.medical_clinic.controller;

import com.PEWUE.medical_clinic.command.DoctorCreateCommand;
import com.PEWUE.medical_clinic.command.DoctorEditCommand;
import com.PEWUE.medical_clinic.dto.DoctorDto;
import com.PEWUE.medical_clinic.dto.ErrorMessageDto;
import com.PEWUE.medical_clinic.dto.PageDto;
import com.PEWUE.medical_clinic.mapper.DoctorMapper;
import com.PEWUE.medical_clinic.mapper.PageMapper;
import com.PEWUE.medical_clinic.model.Doctor;
import com.PEWUE.medical_clinic.service.DoctorService;
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
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/doctors")
@Tag(name = "Doctors operation")
public class DoctorController {
    private final DoctorService doctorService;
    private final DoctorMapper doctorMapper;
    private final PageMapper pageMapper;

    @Operation(summary = "Get all doctors")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "List of doctors returned",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = DoctorDto.class)))),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessageDto.class)))})
    @GetMapping
    public PageDto<DoctorDto> findAll(@ParameterObject Pageable pageable) {
        log.info("Received GET /doctors, pageable={}", pageable);
        Page<Doctor> page = doctorService.find(pageable);
        return pageMapper.toDto(page, doctorMapper::toDto);
    }

    @Operation(summary = "Add doctor to collection")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Doctor created",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DoctorDto.class))),
            @ApiResponse(
                    responseCode = "400",
                    description = "Fields should not be null",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "User with given id not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(
                    responseCode = "409",
                    description = "Given email already exists",
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
    public DoctorDto add(@RequestBody DoctorCreateCommand doctorCreateCommand) {
        log.info("Received POST /doctors to add new doctor: {} {}", doctorCreateCommand.firstName(), doctorCreateCommand.lastName());
        Doctor doctor = doctorMapper.toEntity(doctorCreateCommand);
        return doctorMapper.toDto(doctorService.add(doctor));
    }

    @Operation(summary = "Delete doctor by email")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Doctor deleted"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Doctor not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessageDto.class)))})
    @DeleteMapping("/{email}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String email) {
        log.info("Received DELETE /doctors/{} to delete doctor", email);
        doctorService.delete(email);
    }

    @Operation(summary = "Edit doctor by email")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Doctor edited successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DoctorDto.class))),
            @ApiResponse(
                    responseCode = "400",
                    description = "Fields should not be null",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Doctor not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessageDto.class)))})
    @PutMapping("/{email}")
    public DoctorDto editDoctor(@PathVariable String email, @RequestBody DoctorEditCommand doctorEditCommand) {
        log.info("Received PUT /doctors/{} to edit doctor", email);
        Doctor doctor = doctorMapper.toEntity(doctorEditCommand);
        return doctorMapper.toDto(doctorService.edit(email, doctor));
    }
}
