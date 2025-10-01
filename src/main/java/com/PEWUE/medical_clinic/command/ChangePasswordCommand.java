package com.PEWUE.medical_clinic.command;

import lombok.Builder;

@Builder
public record ChangePasswordCommand(String password) {
}
