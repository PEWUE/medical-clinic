package com.PEWUE.medical_clinic.exception;

import org.springframework.http.HttpStatus;

public class PatientNotFoundException extends MedClinicException {

    public PatientNotFoundException(String email) {
        super("Patient with email " + email + " not found", HttpStatus.NOT_FOUND);
    }

    public PatientNotFoundException(Long id) {
        super("Patient with id " + id + " not found", HttpStatus.NOT_FOUND);
    }
}
