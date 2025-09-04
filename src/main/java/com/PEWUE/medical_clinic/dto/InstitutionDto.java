package com.PEWUE.medical_clinic.dto;

import java.util.List;

public record InstitutionDto(
        Long id,
        String name,
        String city,
        String postalCode,
        String street,
        String streetNo,
        List<Long> doctorsIds) {
}
