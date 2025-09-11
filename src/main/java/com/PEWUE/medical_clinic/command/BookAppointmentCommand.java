package com.PEWUE.medical_clinic.command;

public record BookAppointmentCommand(
        Long appointmentId,
        Long patientId
) {
}
