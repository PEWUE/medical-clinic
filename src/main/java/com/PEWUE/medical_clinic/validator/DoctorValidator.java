package com.PEWUE.medical_clinic.validator;

import com.PEWUE.medical_clinic.exception.EmailAlreadyExistsException;
import com.PEWUE.medical_clinic.model.Doctor;
import com.PEWUE.medical_clinic.repository.DoctorRepository;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DoctorValidator {
    public static void validateCreateDoctor(Doctor doctor, DoctorRepository doctorRepository) {
        if (doctor.getFirstName() == null ||
                doctor.getLastName() == null ||
                doctor.getSpecialization() == null ||
                doctor.getUser() == null) {
            throw new IllegalArgumentException("Fields should not be null");
        }
    }

    public static void validateEditDoctor(Doctor existingDoctor, Doctor updatedDoctor, DoctorRepository doctorRepository) {
        if (updatedDoctor.getFirstName() == null ||
                updatedDoctor.getLastName() == null ||
                updatedDoctor.getSpecialization() == null ||
                updatedDoctor.getUser() == null) {
            throw new IllegalArgumentException("Fields should not be null");
        }
    }
}
