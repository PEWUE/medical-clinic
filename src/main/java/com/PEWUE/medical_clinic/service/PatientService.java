package com.PEWUE.medical_clinic.service;

import com.PEWUE.medical_clinic.exception.PatientNotFoundException;
import com.PEWUE.medical_clinic.model.Patient;
import com.PEWUE.medical_clinic.repository.PatientRepository;
import com.PEWUE.medical_clinic.validator.PatientValidator;
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
                .orElseThrow(() -> new PatientNotFoundException(email));
    }

    public Patient addPatient(Patient patient) {
        PatientValidator.validateCreatePatient(patient, patientRepository);
        return patientRepository.add(patient);
    }

    public void removePatient(String email) {
        if (!patientRepository.remove(email)) {
            throw new PatientNotFoundException(email);
        }
    }

    public Patient editPatient(String email, Patient updatedPatient) {
        Patient patient = patientRepository.findByEmail(email)
                .orElseThrow(() -> new PatientNotFoundException(email));
        PatientValidator.validateEditPatient(patient, updatedPatient, patientRepository);
        return patientRepository.edit(patient, updatedPatient);
    }

    public Patient changePassword(String email, String password) {
        PatientValidator.validatePassword(password);
        Patient patient = patientRepository.findByEmail(email)
                .orElseThrow(() -> new PatientNotFoundException(email));
        patient.setPassword(password);
        return patientRepository.edit(patient, patient);
    }
}