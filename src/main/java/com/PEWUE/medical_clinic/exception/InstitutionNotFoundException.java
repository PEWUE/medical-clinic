package com.PEWUE.medical_clinic.exception;

import org.springframework.http.HttpStatus;

public class InstitutionNotFoundException extends MedClinicException {

    public InstitutionNotFoundException(Long id) {
        super("Institution with given id: " + id + " not found", HttpStatus.NOT_FOUND);
    }
}
