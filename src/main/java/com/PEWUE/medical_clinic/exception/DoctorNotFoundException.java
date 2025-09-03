package com.PEWUE.medical_clinic.exception;

import org.springframework.http.HttpStatus;

public class DoctorNotFoundException extends MedClinicException {
    public DoctorNotFoundException(String email) {
      super("Doctor with email " + email + " not found", HttpStatus.NOT_FOUND);
    }
}
