package com.PEWUE.medical_clinic.service;

import com.PEWUE.medical_clinic.exception.PatientNotFoundException;
import com.PEWUE.medical_clinic.exception.UserNotFoundException;
import com.PEWUE.medical_clinic.model.Patient;
import com.PEWUE.medical_clinic.model.User;
import com.PEWUE.medical_clinic.repository.PatientRepository;
import com.PEWUE.medical_clinic.repository.UserRepository;
import com.PEWUE.medical_clinic.validator.PatientValidator;
import com.PEWUE.medical_clinic.validator.UserValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PatientService {
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;

    public Page<Patient> find(Pageable pageable) {
        log.info("Finding patients with pageable={}", pageable);
        return patientRepository.findAll(pageable);
    }

    public Patient find(String email) {
        log.info("Finding patients with email {}", email);
        return patientRepository.findByUserEmail(email)
                .orElseThrow(() -> new PatientNotFoundException(email));
    }

    @Transactional
    public Patient add(Patient patient) {
        log.info("Adding new patient {} {}", patient.getFirstName(), patient.getLastName());
        PatientValidator.validateCreatePatient(patient, patientRepository);
        assignUserToPatient(patient);
        UserValidator.validateCreateUser(patient.getUser(), userRepository);
        log.info("Successfully added new patient {} {}", patient.getFirstName(), patient.getLastName());
        return patientRepository.save(patient);
    }

    @Transactional
    public void delete(String email) {
        log.info("Deleting patient with email {}", email);
        Patient patient = find(email);
        patientRepository.delete(patient);
        log.info("Patient with email {} was deleted", email);
    }

    @Transactional
    public Patient edit(String email, Patient updatedPatient) {
        log.info("Editing doctor with email {}", email);
        Patient patient = find(email);
        PatientValidator.validateEditPatient(updatedPatient);
        patient.edit(updatedPatient);
        log.info("Successfully edited patient with email {}", email);
        return patientRepository.save(patient);
    }

    private void assignUserToPatient(Patient patient) {
        if (patient.getUser() != null && patient.getUser().getId() != null) {
            log.info("Assigning existing user with id {} to patient", patient.getUser().getId());
            User existingUser = userRepository.findById(patient.getUser().getId())
                    .orElseThrow(() -> new UserNotFoundException(patient.getUser().getId()));
            patient.setUser(existingUser);
        }
    }
}