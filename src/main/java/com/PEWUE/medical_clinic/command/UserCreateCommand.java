package com.PEWUE.medical_clinic.command;

public record UserCreateCommand(
        Long id,
        String email,
        String username,
        String password) {
}
