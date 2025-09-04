package com.PEWUE.medical_clinic.exception;

import org.springframework.http.HttpStatus;

public class FieldsShouldNotBeNullException extends MedClinicException {

    public FieldsShouldNotBeNullException() {
        super("Fields should not be null", HttpStatus.BAD_REQUEST);
    }
}
