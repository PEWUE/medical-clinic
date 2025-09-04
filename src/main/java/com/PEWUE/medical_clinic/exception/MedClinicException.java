package com.PEWUE.medical_clinic.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class MedClinicException extends RuntimeException {
    private final HttpStatus status;

    public MedClinicException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
