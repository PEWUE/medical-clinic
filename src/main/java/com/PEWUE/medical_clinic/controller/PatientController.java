package com.PEWUE.medical_clinic.controller;

import com.PEWUE.medical_clinic.command.PatientCreateCommand;
import com.PEWUE.medical_clinic.command.PatientEditCommand;
import com.PEWUE.medical_clinic.dto.ErrorMessageDto;
import com.PEWUE.medical_clinic.dto.PatientDto;
import com.PEWUE.medical_clinic.mapper.PatientMapper;
import com.PEWUE.medical_clinic.model.Patient;
import com.PEWUE.medical_clinic.service.PatientService;
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
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/patients")
@Tag(name = "Patients operations")
public class PatientController {
    private final PatientService patientService;
    private final PatientMapper patientMapper;

    @Operation(summary = "Get all patients")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "List of patients returned",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = PatientDto.class)))),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessageDto.class)))})
    @GetMapping
    public List<PatientDto> findAll() {
        return patientService.findAll().stream()
                .map(patientMapper::toDto)
                .collect(Collectors.toList());
    }

    @Operation(summary = "Get patient by email")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Patient found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PatientDto.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Patient not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessageDto.class)))})
    @GetMapping("/{email}")
    public PatientDto find(@PathVariable String email) {
        return patientMapper.toDto(patientService.find(email));
    }

    @Operation(summary = "Add patient to collection")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Patient created",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PatientDto.class))),
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
                    description = "Given email or id card number already exists",
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
    public PatientDto add(@RequestBody PatientCreateCommand patientCreateCommand) {
        Patient patient = patientMapper.toEntity(patientCreateCommand);
        return patientMapper.toDto(patientService.add(patient));
    }

    @Operation(summary = "Delete patient by email")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Patient deleted"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Patient not found",
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
        patientService.delete(email);
    }

    @Operation(summary = "Edit patient by email")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Patient edited successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PatientDto.class))),
            @ApiResponse(
                    responseCode = "400",
                    description = "Fields should not be null",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Patient not found",
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
    public PatientDto edit(@PathVariable String email, @RequestBody PatientEditCommand patientEditCommand) {
        Patient patient = patientMapper.toEntity(patientEditCommand);
        return patientMapper.toDto(patientService.edit(email, patient));
    }
}
