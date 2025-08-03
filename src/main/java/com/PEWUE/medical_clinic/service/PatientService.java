package com.PEWUE.medical_clinic.service;

import com.PEWUE.medical_clinic.exceptions.EmailAlreadyExistsException;
import com.PEWUE.medical_clinic.exceptions.PatientNotFoundException;
import com.PEWUE.medical_clinic.model.Patient;
import com.PEWUE.medical_clinic.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientService {
    private final PatientRepository patientRepository;

    public List<Patient> getAllPatients() {
        return patientRepository.getAll();
    }

    public Patient getPatientByEmail(String email) {
        return patientRepository.findByEmail(email)
                .orElseThrow(() -> new PatientNotFoundException("Patient with email " + email + " not found"));
    }

    public void addPatient(Patient patient) {
        boolean added = patientRepository.add(patient);
        if (!added) {
            throw new EmailAlreadyExistsException("Email " + patient.getEmail() + " is already taken");
        }
    }

    public void removePatient(String email) {
        boolean removed = patientRepository.remove(email);
        if (!removed) {
            throw new PatientNotFoundException("Patient with email " + email + " does not exist");
        }
    }

    public void editPatient(String email, Patient updatedPatient) {
        boolean edited = patientRepository.edit(email, updatedPatient);
        if (!edited) {
            if (patientRepository.findByEmail(email).isEmpty()) {
                throw new PatientNotFoundException("Patient with email " + email + " does not exist");
            } else {
                throw new EmailAlreadyExistsException("Email " + updatedPatient.getEmail() + " is already taken");
            }
        }
    }
}
