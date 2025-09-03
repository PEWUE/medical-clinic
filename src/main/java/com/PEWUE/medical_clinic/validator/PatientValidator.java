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
                patient.getIdCardNo() == null ||
                patient.getPhoneNumber() == null ||
                patient.getBirthday() == null ||
                patient.getUser() == null) {
            throw new IllegalArgumentException("Fields should not be null");
        }
        if (patientRepository.findByEmail(patient.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException(patient.getEmail());
        }
    }

    public static void validateEditPatient(Patient existingPatient, Patient updatedPatient, PatientRepository patientRepository) {
        if (updatedPatient.getFirstName() == null ||
                updatedPatient.getLastName() == null ||
                updatedPatient.getEmail() == null ||
                updatedPatient.getIdCardNo() == null ||
                updatedPatient.getPhoneNumber() == null ||
                updatedPatient.getBirthday() == null ||
                updatedPatient.getUser() == null) {
            throw new IllegalArgumentException("Fields should not be null");
        }
        if (!existingPatient.getEmail().equals(updatedPatient.getEmail()) && patientRepository.findByEmail(updatedPatient.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException(updatedPatient.getEmail());
        }
        if (!existingPatient.getIdCardNo().equals(updatedPatient.getIdCardNo())) {
            throw new IllegalArgumentException("Changing the ID card number is not allowed");
        }
    }
}
