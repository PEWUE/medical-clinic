package com.PEWUE.medical_clinic.controller;

import com.PEWUE.medical_clinic.command.InstitutionCreateCommand;
import com.PEWUE.medical_clinic.dto.ErrorMessageDto;
import com.PEWUE.medical_clinic.dto.InstitutionDto;
import com.PEWUE.medical_clinic.dto.PageDto;
import com.PEWUE.medical_clinic.mapper.InstitutionMapper;
import com.PEWUE.medical_clinic.mapper.PageMapper;
import com.PEWUE.medical_clinic.model.Institution;
import com.PEWUE.medical_clinic.service.InstitutionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/institutions")
@Tag(name = "Institutions operations")
public class InstitutionController {
    private final InstitutionService institutionService;
    private final InstitutionMapper institutionMapper;
    private final PageMapper pageMapper;

    @Operation(summary = "Get all institutions")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "List of institutions returned",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = InstitutionDto.class)))),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessageDto.class)))})
    @GetMapping
    public PageDto<InstitutionDto> find(Pageable pageable) {
        Page<Institution> page = institutionService.find(pageable);
        return pageMapper.toPageDto(page, institutionMapper::toDto);
    }

    @Operation(summary = "Add institution to collection")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Institution created",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = InstitutionDto.class))),
            @ApiResponse(
                    responseCode = "400",
                    description = "Fields should not be null",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(
                    responseCode = "409",
                    description = "Given institution name already exists",
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
    public InstitutionDto add(@RequestBody InstitutionCreateCommand institutionCreateCommand) {
        Institution institution = institutionMapper.toEntity(institutionCreateCommand);
        return institutionMapper.toDto(institutionService.add(institution));
    }

    @Operation(summary = "Delete institution by id")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Institution deleted"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Institution not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessageDto.class)))})
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        institutionService.delete(id);
    }

    @Operation(summary = "Add doctor to institution")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Doctor added to institution",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = InstitutionDto.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Institution or doctor not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessageDto.class)))})
    @PutMapping("/{institutionId}/doctors/{doctorId}")
    public InstitutionDto assignDoctorToInstitution(@PathVariable Long doctorId, @PathVariable Long institutionId) {
        return institutionMapper.toDto(institutionService.assignDoctorToInstitution(doctorId, institutionId));
    }
}
