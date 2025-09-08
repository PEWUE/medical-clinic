package com.PEWUE.medical_clinic.command;

import java.time.LocalDateTime;

public record AppointmentCreateCommand(
        Long doctorId,
        LocalDateTime startTime,
        LocalDateTime endTime
) {
}
