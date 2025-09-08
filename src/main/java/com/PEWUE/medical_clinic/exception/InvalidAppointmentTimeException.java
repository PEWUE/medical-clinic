package com.PEWUE.medical_clinic.exception;

import org.springframework.http.HttpStatus;

public class InvalidAppointmentTimeException extends MedClinicException {

    public InvalidAppointmentTimeException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
