package com.PEWUE.medical_clinic.exception;

import com.PEWUE.medical_clinic.model.Appointment;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class AppointmentOverlapException extends MedClinicException {

    private final List<Appointment> conflicts;

    public AppointmentOverlapException(String message) {
        super(message, HttpStatus.CONFLICT);
        this.conflicts = new ArrayList<>();
    }

    public AppointmentOverlapException(List<Appointment> conflicts) {
        super(buildMessage(conflicts), HttpStatus.CONFLICT);
        this.conflicts = conflicts;
    }

    private static String buildMessage(List<Appointment> conflicts) {
        return "The appointment time overlaps with existing appointments: " +
                conflicts.stream()
                        .map(a -> "Appointment[id=" + a.getId() + ", start=" + a.getStartTime() + ", end=" + a.getEndTime() + "]")
                        .collect(Collectors.joining("; "));
    }
}
