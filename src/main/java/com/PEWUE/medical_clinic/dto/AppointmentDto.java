package com.PEWUE.medical_clinic.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record AppointmentDto(
        Long id,
        Long doctorId,
        Long patientId,
        LocalDateTime startTime,
        LocalDateTime endTime
) {
}
