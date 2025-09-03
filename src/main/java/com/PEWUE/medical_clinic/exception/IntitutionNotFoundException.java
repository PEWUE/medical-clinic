package com.PEWUE.medical_clinic.exception;

import org.springframework.http.HttpStatus;

public class IntitutionNotFoundException extends MedClinicException {
    public IntitutionNotFoundException(Long id) {
        super("Institution with given id: " + id + " not found", HttpStatus.NOT_FOUND);
    }
}
