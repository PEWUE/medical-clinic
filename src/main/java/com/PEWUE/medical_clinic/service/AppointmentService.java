package com.PEWUE.medical_clinic.service;

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
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    public List<Appointment> find(Long doctorId, Long patientId) {
        if (doctorId != null && patientId != null) {
            return appointmentRepository.findByDoctorIdAndPatientId(doctorId, patientId);
        } else if (doctorId != null) {
            return appointmentRepository.findByDoctorId(doctorId);
        } else if (patientId != null) {
            return appointmentRepository.findByPatientId(patientId);
        } else {
            return appointmentRepository.findAll();
        }
    }

    @Transactional
    public Appointment add(Long doctorId, LocalDateTime startTime, LocalDateTime endTime) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new DoctorNotFoundException(doctorId));
        Appointment freeSlot = Appointment.builder()
                .doctor(doctor)
                .patient(null)
                .startTime(startTime)
                .endTime(endTime)
                .build();
        AppointmentValidator.validateCreateAppointment(freeSlot, appointmentRepository);
        return appointmentRepository.save(freeSlot);
    }

    @Transactional
    public Appointment book(Long appointmentId, Long patientId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new AppointmentNotFoundException(appointmentId));
        AppointmentValidator.validateBookAppointment(appointment);
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new PatientNotFoundException(patientId));
        appointment.setPatient(patient);
        return appointmentRepository.save(appointment);
    }

    public List<Appointment> findAllForPatient(Long patientId) {
        patientRepository.findById(patientId).orElseThrow(() -> new PatientNotFoundException(patientId));
        return appointmentRepository.findByPatientId(patientId);
    }

    public List<Appointment> findAllForDoctor(Long doctorId) {
        doctorRepository.findById(doctorId).orElseThrow(() -> new DoctorNotFoundException(doctorId));
        return appointmentRepository.findByDoctorId(doctorId);
    }
}
