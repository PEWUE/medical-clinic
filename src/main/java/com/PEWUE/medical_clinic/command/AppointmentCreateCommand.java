package com.PEWUE.medical_clinic.command;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record AppointmentCreateCommand(
        Long doctorId,
        LocalDateTime startTime,
        LocalDateTime endTime
) {
}
