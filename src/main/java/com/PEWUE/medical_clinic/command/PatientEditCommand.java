package com.PEWUE.medical_clinic.command;

import java.time.LocalDate;

public record PatientEditCommand(
        String firstName,
        String lastName,
        String phoneNumber,
        LocalDate birthday
) {
}
