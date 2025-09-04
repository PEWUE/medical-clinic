package com.PEWUE.medical_clinic.command;

public record DoctorEditCommand(
        String firstName,
        String lastName,
        String specialization
) {
}
