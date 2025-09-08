package com.PEWUE.medical_clinic.exception;

import org.springframework.http.HttpStatus;

public class AppointmentNotFoundException extends MedClinicException {

    public AppointmentNotFoundException(Long appointmentId) {
        super("Appointment with id " + appointmentId + " not found", HttpStatus.NOT_FOUND);
    }
}
