package com.PEWUE.medical_clinic.controller;

import com.PEWUE.medical_clinic.command.DoctorCreateCommand;
import com.PEWUE.medical_clinic.command.DoctorEditCommand;
import com.PEWUE.medical_clinic.command.PatientEditCommand;
import com.PEWUE.medical_clinic.dto.DoctorDto;
import com.PEWUE.medical_clinic.dto.ErrorMessageDto;
import com.PEWUE.medical_clinic.dto.PatientDto;
import com.PEWUE.medical_clinic.mapper.DoctorMapper;
import com.PEWUE.medical_clinic.model.Doctor;
import com.PEWUE.medical_clinic.model.Patient;
import com.PEWUE.medical_clinic.service.DoctorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/doctors")
@Tag(name = "Doctors operation")
public class DoctorController {
    private final DoctorService doctorService;
    private final DoctorMapper doctorMapper;


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
    public List<DoctorDto> getDoctors() {
        return doctorService.getAllDoctors().stream()
                .map(doctorMapper::toDto)
                .toList();
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
    public DoctorDto addDoctor(@RequestBody DoctorCreateCommand doctorCreateCommand) {
        Doctor doctor = doctorMapper.toEntity(doctorCreateCommand);
        return doctorMapper.toDto(doctorService.addDoctor(doctor));
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
    public void removeDoctor(@PathVariable String email) {
        doctorService.removeDoctor(email);
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
        Doctor doctor = doctorMapper.toEntity(doctorEditCommand);
        return doctorMapper.toDto(doctorService.editDoctor(email, doctor));
    }
}
