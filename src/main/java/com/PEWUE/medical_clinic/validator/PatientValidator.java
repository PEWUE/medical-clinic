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
                patient.getIdCardNo() == null ||
                patient.getPhoneNumber() == null ||
                patient.getBirthday() == null ||
                patient.getUser() == null) {
            throw new IllegalArgumentException("Fields should not be null");
        }
        if (patientRepository.findByIdCardNo(patient.getIdCardNo()).isPresent()) {
            throw new IllegalArgumentException("ID card number already exists");
        }
    }

    public static void validateEditPatient(Patient updatedPatient) {
        if (updatedPatient.getFirstName() == null ||
                updatedPatient.getLastName() == null ||
                updatedPatient.getPhoneNumber() == null ||
                updatedPatient.getBirthday() == null) {
            throw new IllegalArgumentException("Fields should not be null");
        }
    }
}
