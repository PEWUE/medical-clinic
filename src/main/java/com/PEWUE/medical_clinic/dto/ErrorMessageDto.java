package com.PEWUE.medical_clinic.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ErrorMessageDto(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path
) {}