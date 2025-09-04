package com.PEWUE.medical_clinic.validator;

import com.PEWUE.medical_clinic.exception.FieldsShouldNotBeNullException;
import com.PEWUE.medical_clinic.exception.InstitutionNameAlreadyExistsException;
import com.PEWUE.medical_clinic.model.Institution;
import com.PEWUE.medical_clinic.repository.InstitutionRepository;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class InstitutionValidator {

    public static void validateCreateInstitution(Institution institution, InstitutionRepository institutionRepository) {
        if (institution.getName() == null ||
                institution.getCity() == null ||
                institution.getPostalCode() == null ||
                institution.getStreet() == null ||
                institution.getStreetNo() == null) {
            throw new FieldsShouldNotBeNullException();
        }
        if (institutionRepository.findByName(institution.getName()).isPresent()) {
            throw new InstitutionNameAlreadyExistsException(institution.getName());
        }
    }
}
