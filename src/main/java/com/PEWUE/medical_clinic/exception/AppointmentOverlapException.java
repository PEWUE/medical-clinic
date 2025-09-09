package com.PEWUE.medical_clinic.exception;

import com.PEWUE.medical_clinic.model.Appointment;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.stream.Collectors;

public class AppointmentOverlapException extends MedClinicException {

    public AppointmentOverlapException(String message) {
        super(message, HttpStatus.CONFLICT);
    }

    public AppointmentOverlapException(List<Appointment> conflicts) {
        super(buildMessage(conflicts), HttpStatus.CONFLICT);
    }

    private static String buildMessage(List<Appointment> conflicts) {
        return "The appointment time overlaps with existing appointments: " +
                conflicts.stream()
                        .map(a -> "Appointment[id=" + a.getId() + ", start=" + a.getStartTime() + ", end=" + a.getEndTime() + "]")
                        .collect(Collectors.joining("; "));
    }
}
