package com.PEWUE.medical_clinic.command;

import lombok.Builder;

@Builder
public record DoctorEditCommand(
        String firstName,
        String lastName,
        String specialization
) {
}
