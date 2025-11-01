package com.PEWUE.medical_clinic.service;

import com.PEWUE.medical_clinic.command.AppointmentCreateCommand;
import com.PEWUE.medical_clinic.command.BookAppointmentCommand;
import com.PEWUE.medical_clinic.exception.AppointmentNotFoundException;
import com.PEWUE.medical_clinic.exception.DoctorNotFoundException;
import com.PEWUE.medical_clinic.exception.InvalidAppointmentTimeException;
import com.PEWUE.medical_clinic.exception.PatientNotFoundException;
import com.PEWUE.medical_clinic.model.Appointment;
import com.PEWUE.medical_clinic.model.Doctor;
import com.PEWUE.medical_clinic.model.Patient;
import com.PEWUE.medical_clinic.repository.AppointmentRepository;
import com.PEWUE.medical_clinic.repository.DoctorRepository;
import com.PEWUE.medical_clinic.repository.PatientRepository;
import com.PEWUE.medical_clinic.validator.AppointmentValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    public Page<Appointment> findAppointments(
            Long doctorId,
            Long patientId,
            String specialization,
            LocalDateTime from,
            LocalDateTime to,
            Boolean freeSlots,
            Pageable pageable) {

        Specification<Appointment> spec = (root, query, cb) -> null;

        if (doctorId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("doctor").get("id"), doctorId));
        }
        if (patientId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("patient").get("id"), patientId));
        }
        if (specialization != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("doctor").get("specialization"), specialization));
        }
        if (from != null && to != null) {
            spec = spec.and((root, query, cb) ->
                    cb.between(root.get("date"), from, to));
        }
        if (freeSlots != null && freeSlots) {
            spec = spec.and((root, query, cb) -> cb.isNull(root.get("patient")));
        }

        return appointmentRepository.findAll(spec, pageable);
    }

    public Appointment findById(Long appointmentId) {
        log.info("Finding appointments by id={}", appointmentId);
        return appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new AppointmentNotFoundException(appointmentId));
    }

    @Transactional
    public Appointment add(AppointmentCreateCommand command) {
        log.info("Creating new appointment for doctorId={}", command.doctorId());
        Doctor doctor = doctorRepository.findById(command.doctorId())
                .orElseThrow(() -> new DoctorNotFoundException(command.doctorId()));
        Appointment freeSlot = Appointment.createNewAppointment(command);
        freeSlot.setDoctor(doctor);
        AppointmentValidator.validateCreateAppointment(freeSlot, appointmentRepository);
        log.info("Successfully created appointment for doctorId={}, date={}", command.doctorId(), freeSlot.getStartTime());
        return appointmentRepository.save(freeSlot);
    }

    @Transactional
    public Appointment book(BookAppointmentCommand command) {
        log.info("Booking appointmentId={} for patientId={}", command.appointmentId(), command.patientId());
        Appointment appointment = appointmentRepository.findById(command.appointmentId())
                .orElseThrow(() -> new AppointmentNotFoundException(command.appointmentId()));
        AppointmentValidator.validateBookAppointment(appointment);
        Patient patient = patientRepository.findById(command.patientId())
                .orElseThrow(() -> new PatientNotFoundException(command.patientId()));
        appointment.setPatient(patient);
        log.info("AppointmentId={} successfully booked for patientId={}", command.appointmentId(), command.patientId());
        return appointmentRepository.save(appointment);
    }

    @Transactional
    public void cancel(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new AppointmentNotFoundException(appointmentId));
        appointmentRepository.delete(appointment);
    }
}