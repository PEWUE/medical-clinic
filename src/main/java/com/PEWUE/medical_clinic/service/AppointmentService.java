package com.PEWUE.medical_clinic.service;

import com.PEWUE.medical_clinic.command.AppointmentCreateCommand;
import com.PEWUE.medical_clinic.command.BookAppointmentCommand;
import com.PEWUE.medical_clinic.exception.AppointmentNotFoundException;
import com.PEWUE.medical_clinic.exception.DoctorNotFoundException;
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

    public Page<Appointment> find(Long doctorId, Long patientId, Pageable pageable) {
        log.info("Finding appointments by doctorId={}, patientId={}, pageable={}", doctorId, patientId, pageable);
        return appointmentRepository.findByFilters(doctorId, patientId, pageable);
    }

    public Page<Appointment> findFreeSlots(Long doctorId, Pageable pageable) {
        log.info("Finding free appointment slots by doctorId={}", doctorId);
        return appointmentRepository.findFreeAppointmentsFromNow(doctorId, LocalDateTime.now(), pageable);
    }

    public Page<Appointment> findFreeAppointmentsBySpecializationAndDay(String specialization, LocalDate date, Pageable pageable) {
        log.info("Finding free appointment slots, specialization={}, date={}", specialization, date);
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();
        return appointmentRepository.findFreeAppointmentsBySpecializationAndDay(specialization, startOfDay, endOfDay, pageable);
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
}