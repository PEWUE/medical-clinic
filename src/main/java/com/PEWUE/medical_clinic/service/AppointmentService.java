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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    public Page<Appointment> find(Long doctorId, Long patientId, Pageable pageable) {
        return appointmentRepository.findByFilters(doctorId, patientId, pageable);
    }

    @Transactional
    public Appointment add(AppointmentCreateCommand command) {
        Doctor doctor = doctorRepository.findById(command.doctorId())
                .orElseThrow(() -> new DoctorNotFoundException(command.doctorId()));
        Appointment freeSlot = Appointment.createNewAppointment(command);
        freeSlot.setDoctor(doctor);
        AppointmentValidator.validateCreateAppointment(freeSlot, appointmentRepository);
        return appointmentRepository.save(freeSlot);
    }

    @Transactional
    public Appointment book(BookAppointmentCommand command) {
        Appointment appointment = appointmentRepository.findById(command.appointmentId())
                .orElseThrow(() -> new AppointmentNotFoundException(command.appointmentId()));
        AppointmentValidator.validateBookAppointment(appointment);
        Patient patient = patientRepository.findById(command.patientId())
                .orElseThrow(() -> new PatientNotFoundException(command.patientId()));
        appointment.setPatient(patient);
        return appointmentRepository.save(appointment);
    }

}