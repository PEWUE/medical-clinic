package com.PEWUE.medical_clinic.controller;

import com.PEWUE.medical_clinic.command.ChangePasswordCommand;
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
import org.springframework.web.bind.annotation.PatchMapping;
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
    public List<PatientDto> getPatients() {
        return patientService.getAllPatients().stream()
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
                            schema = @Schema(implementation = ErrorMessageDto.class)))})
    @GetMapping("/{email}")
    public PatientDto getPatientByEmail(@PathVariable String email) {
        return patientMapper.toDto(patientService.getPatientByEmail(email));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PatientDto addPatient(@RequestBody Patient patient) {
        return patientMapper.toDto(patientService.addPatient(patient));
    }

    @DeleteMapping("/{email}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removePatient(@PathVariable String email) {
        patientService.removePatient(email);
    }

    @PutMapping("/{email}")
    public PatientDto editPatient(@PathVariable String email, @RequestBody Patient patient) {
        return patientMapper.toDto(patientService.editPatient(email, patient));
    }

    @PatchMapping("/{email}")
    public PatientDto changePassword(@PathVariable String email, @RequestBody ChangePasswordCommand command) {
        return patientMapper.toDto(patientService.changePassword(email, command.getPassword()));
    }
}
