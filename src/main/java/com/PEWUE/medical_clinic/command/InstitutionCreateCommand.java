package com.PEWUE.medical_clinic.command;

import lombok.Builder;

@Builder
public record InstitutionCreateCommand(
        String name,
        String city,
        String postalCode,
        String street,
        String streetNo
) {
}
