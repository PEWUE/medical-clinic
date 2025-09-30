package com.PEWUE.medical_clinic.command;

import lombok.Builder;

@Builder
public record UserCreateCommand(
        Long id,
        String email,
        String username,
        String password) {
}
