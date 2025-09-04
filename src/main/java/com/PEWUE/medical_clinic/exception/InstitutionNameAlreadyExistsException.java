package com.PEWUE.medical_clinic.exception;

import org.springframework.http.HttpStatus;

public class InstitutionNameAlreadyExistsException extends MedClinicException {

    public InstitutionNameAlreadyExistsException(String name) {
        super("Institution name: " + name + " is already taken", HttpStatus.CONFLICT);
    }
}
