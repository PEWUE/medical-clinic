package com.PEWUE.medical_clinic.command;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record PatientEditCommand(
        String firstName,
        String lastName,
        String phoneNumber,
        LocalDate birthday
) {
}
