package com.PEWUE.medical_clinic.exception;

import org.springframework.http.HttpStatus;

public class AppointmentAlreadyBookedException extends MedClinicException {
    public AppointmentAlreadyBookedException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
