package com.PEWUE.medical_clinic.exception;

import org.springframework.http.HttpStatus;

public class EmailAlreadyExistsException extends MedClinicException {

    public EmailAlreadyExistsException(String email) {
        super("Email " + email + " is already taken", HttpStatus.CONFLICT);
    }
}
