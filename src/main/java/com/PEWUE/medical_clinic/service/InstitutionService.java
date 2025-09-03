package com.PEWUE.medical_clinic.service;

import com.PEWUE.medical_clinic.exception.IntitutionNotFoundException;
import com.PEWUE.medical_clinic.model.Doctor;
import com.PEWUE.medical_clinic.model.Institution;
import com.PEWUE.medical_clinic.repository.InstitutionRepository;
import com.PEWUE.medical_clinic.validator.InstitutionValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InstitutionService {
    private final InstitutionRepository institutionRepository;

    public List<Institution> getAllInstitutions() {
        return institutionRepository.findAll();
    }

    public Institution addInstitution(Institution institution) {
        InstitutionValidator.validateCreateInstitution(institution, institutionRepository);
        return institutionRepository.save(institution);
    }

    public void removeInstitution(Long id) {
        Institution institution = institutionRepository.findById(id)
                .orElseThrow(() -> new IntitutionNotFoundException(id));
        institutionRepository.delete(institution);
    }
}
