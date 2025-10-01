package com.PEWUE.medical_clinic.command;

import lombok.Builder;

@Builder
public record BookAppointmentCommand(
        Long appointmentId,
        Long patientId
) {
}
