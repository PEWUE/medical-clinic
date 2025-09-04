package com.PEWUE.medical_clinic.exception;

import org.springframework.http.HttpStatus;

public class UsernameAlreadyExistsException extends MedClinicException {

    public UsernameAlreadyExistsException(String username) {
        super("Username " + username + " is already taken", HttpStatus.CONFLICT);
    }
}
