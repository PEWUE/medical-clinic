package com.PEWUE.medical_clinic.exception;

import org.springframework.http.HttpStatus;

public class AppointmentOverlapException extends MedClinicException {

    public AppointmentOverlapException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
