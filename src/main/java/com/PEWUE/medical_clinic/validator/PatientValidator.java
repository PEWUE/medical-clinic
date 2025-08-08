package com.PEWUE.medical_clinic.validator;

import com.PEWUE.medical_clinic.exception.EmailAlreadyExistsException;
import com.PEWUE.medical_clinic.model.Patient;
import com.PEWUE.medical_clinic.repository.PatientRepository;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PatientValidator {
    public static void validateCreatePatient(Patient patient, PatientRepository patientRepository) {
        if (patient.getFirstName() == null ||
                patient.getLastName() == null ||
                patient.getEmail() == null ||
                patient.getPassword() == null ||
                patient.getIdCardNo() == null ||
                patient.getPhoneNumber() == null ||
                patient.getBirthday() == null) {
            throw new IllegalArgumentException("Fields should not be null");
        }
        if (patientRepository.findByEmail(patient.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Email " + patient.getEmail() + " is already taken");
        }
    }

    public static void validateEditPatient(Patient existingPatient, Patient updatedPatient, PatientRepository patientRepository) {
        if (updatedPatient.getFirstName() == null ||
                updatedPatient.getLastName() == null ||
                updatedPatient.getEmail() == null ||
                updatedPatient.getPassword() == null ||
                updatedPatient.getIdCardNo() == null ||
                updatedPatient.getPhoneNumber() == null ||
                updatedPatient.getBirthday() == null) {
            throw new IllegalArgumentException("Fields should not be null");
        }
        if (!existingPatient.getEmail().equals(updatedPatient.getEmail()) && patientRepository.findByEmail(updatedPatient.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Email " + updatedPatient.getEmail() + " is already taken");
        }
        if (!existingPatient.getIdCardNo().equals(updatedPatient.getIdCardNo())) {
            throw new IllegalArgumentException("Changing the ID card number is not allowed");
        }
    }

    public static void validatePassword(String password) {
        if (password == null) {
            throw new IllegalArgumentException("Field should not be null");
        }
    }
}
