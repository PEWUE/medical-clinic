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

    public List<Patient> findAll() {
        return patientRepository.findAll();
    }

    public Patient find(String email) {
        return patientRepository.findByUserEmail(email)
                .orElseThrow(() -> new PatientNotFoundException(email));
    }

    public Patient add(Patient patient) {
        PatientValidator.validateCreatePatient(patient, patientRepository);
        assignUserToPatient(patient);
        UserValidator.validateCreateUser(patient.getUser(), userRepository);
        return patientRepository.save(patient);
    }

    public void delete(String email) {
        Patient patient = find(email);
        patientRepository.delete(patient);
    }

    public Patient edit(String email, Patient updatedPatient) {
        Patient patient = find(email);
        PatientValidator.validateEditPatient(updatedPatient);
        patient.edit(updatedPatient);
        return patientRepository.save(patient);
    }

    private void assignUserToPatient(Patient patient) {
        if (patient.getUser() != null && patient.getUser().getId() != null) {
            User existingUser = userRepository.findById(patient.getUser().getId())
                    .orElseThrow(() -> new UserNotFoundException(patient.getUser().getId()));
            patient.setUser(existingUser);
        }
    }
}