package com.PEWUE.medical_clinic.service;

import com.PEWUE.medical_clinic.exception.DoctorNotFoundException;
import com.PEWUE.medical_clinic.exception.IntitutionNotFoundException;
import com.PEWUE.medical_clinic.model.Doctor;
import com.PEWUE.medical_clinic.model.Institution;
import com.PEWUE.medical_clinic.repository.DoctorRepository;
import com.PEWUE.medical_clinic.repository.InstitutionRepository;
import com.PEWUE.medical_clinic.validator.InstitutionValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InstitutionService {
    private final InstitutionRepository institutionRepository;
    private final DoctorRepository doctorRepository;

    public Page<Institution> find(Pageable pageable) {
        return institutionRepository.findAll(pageable);
    }

    @Transactional
    public Institution add(Institution institution) {
        InstitutionValidator.validateCreateInstitution(institution, institutionRepository);
        return institutionRepository.save(institution);
    }

    @Transactional
    public void delete(Long id) {
        Institution institution = institutionRepository.findById(id)
                .orElseThrow(() -> new IntitutionNotFoundException(id));
        institutionRepository.delete(institution);
    }

    @Transactional
    public Institution assignDoctorToInstitution(Long doctorId, Long institutionId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new DoctorNotFoundException(doctorId));
        Institution institution = institutionRepository.findById(institutionId)
                .orElseThrow(()-> new IntitutionNotFoundException(institutionId));
        institution.getDoctors().add(doctor);
        return institutionRepository.save(institution);
    }
}
