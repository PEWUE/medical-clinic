package com.PEWUE.medical_clinic.exception;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends MedClinicException {

    public UserNotFoundException(Long id) {
        super("User with id " + id + " not found", HttpStatus.NOT_FOUND);
    }
}
