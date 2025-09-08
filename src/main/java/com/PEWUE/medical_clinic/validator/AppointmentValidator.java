package com.PEWUE.medical_clinic.validator;

import com.PEWUE.medical_clinic.exception.AppointmentAlreadyBookedException;
import com.PEWUE.medical_clinic.exception.AppointmentOverlapException;
import com.PEWUE.medical_clinic.exception.InvalidAppointmentTimeException;
import com.PEWUE.medical_clinic.model.Appointment;
import com.PEWUE.medical_clinic.repository.AppointmentRepository;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AppointmentValidator {

    public static void validateCreateAppointment(Appointment appointment, AppointmentRepository appointmentRepository) {
        if (appointment.getStartTime().isAfter(appointment.getEndTime())) {
            throw new InvalidAppointmentTimeException("Appointment start time must be before the end time");
        }

        if (appointment.getStartTime().isBefore(LocalDateTime.now())) {
            throw new InvalidAppointmentTimeException("Appointment start time must be in the future");
        }

        if (appointment.getStartTime().getMinute() % 15 != 0 || appointment.getEndTime().getMinute() % 15 != 0) {
            throw new InvalidAppointmentTimeException("Appointment times must be aligned to 15-minute intervals");
        }

        if (appointmentRepository.existsByDoctorIdAndTimeRange(appointment.getDoctor().getId(), appointment.getStartTime(), appointment.getEndTime())) {
            throw new AppointmentOverlapException("The appointment time overlaps with an existing appointment");
        }
    }

    public static void validateBookAppointment(Appointment appointment) {
        if (appointment.getPatient() != null) {
            throw new AppointmentAlreadyBookedException("The appointment is already taken");
        }
        if (appointment.getStartTime().isBefore(LocalDateTime.now())) {
            throw new InvalidAppointmentTimeException("Appointment start time must be in the future");
        }
    }
}



