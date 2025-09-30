package com.PEWUE.medical_clinic.dto;

import lombok.Builder;

@Builder
public record UserDto(
        Long id,
        String email,
        String username) {
}
