package com.PEWUE.medical_clinic.service;

import com.PEWUE.medical_clinic.exception.PatientNotFoundException;
import com.PEWUE.medical_clinic.exception.UserNotFoundException;
import com.PEWUE.medical_clinic.model.Patient;
import com.PEWUE.medical_clinic.model.User;
import com.PEWUE.medical_clinic.repository.PatientRepository;
import com.PEWUE.medical_clinic.repository.UserRepository;
import com.PEWUE.medical_clinic.validator.PatientValidator;
import com.PEWUE.medical_clinic.validator.UserValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientService {
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;

    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    public Patient getPatientByEmail(String email) {
        return patientRepository.findByEmail(email)
                .orElseThrow(() -> new PatientNotFoundException(email));
    }

    public Patient addPatient(Patient patient) {
        PatientValidator.validateCreatePatient(patient, patientRepository);
        if (patient.getUser() != null) {
            if (patient.getUser().getId() != null) {
                User existingUser = userRepository.findById(patient.getUser().getId())
                        .orElseThrow(() -> new UserNotFoundException(patient.getUser().getId()));
                patient.setUser(existingUser);
            } else {
                patient.getUser().setId(null);
            }
        }
        UserValidator.validateCreateUser(patient.getUser(), userRepository);
        return patientRepository.save(patient);
    }

    public void removePatient(String email) {
        Patient patient = patientRepository.findByEmail(email).orElseThrow(() -> new PatientNotFoundException(email));
        patientRepository.delete(patient);
    }

    public Patient editPatient(String email, Patient updatedPatient) {
        Patient patient = patientRepository.findByEmail(email)
                .orElseThrow(() -> new PatientNotFoundException(email));
        PatientValidator.validateEditPatient(patient, updatedPatient, patientRepository);
        UserValidator.validateEditUser(updatedPatient.getUser(), userRepository);
        patient.edit(updatedPatient);
        return patientRepository.save(patient);
    }

    public Patient changePassword(String email, String password) {
        UserValidator.validatePassword(password);
        Patient patient = patientRepository.findByEmail(email)
                .orElseThrow(() -> new PatientNotFoundException(email));
        patient.getUser().setPassword(password);
        return patientRepository.save(patient);
    }
}