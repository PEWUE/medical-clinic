package com.PEWUE.medical_clinic.validator;

import com.PEWUE.medical_clinic.exception.FieldsShouldNotBeNullException;
import com.PEWUE.medical_clinic.model.Doctor;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DoctorValidator {

    public static void validateCreateDoctor(Doctor doctor) {
        if (doctor.getFirstName() == null ||
                doctor.getLastName() == null ||
                doctor.getSpecialization() == null ||
                doctor.getUser() == null) {
            throw new FieldsShouldNotBeNullException();
        }
    }

    public static void validateEditDoctor(Doctor updatedDoctor) {
        if (updatedDoctor.getFirstName() == null ||
                updatedDoctor.getLastName() == null ||
                updatedDoctor.getSpecialization() == null) {
            throw new FieldsShouldNotBeNullException();
        }
    }
}
