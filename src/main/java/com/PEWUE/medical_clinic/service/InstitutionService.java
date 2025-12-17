package com.PEWUE.medical_clinic.service;

import com.PEWUE.medical_clinic.exception.DoctorNotFoundException;
import com.PEWUE.medical_clinic.exception.InstitutionNotFoundException;
import com.PEWUE.medical_clinic.model.Doctor;
import com.PEWUE.medical_clinic.model.Institution;
import com.PEWUE.medical_clinic.repository.DoctorRepository;
import com.PEWUE.medical_clinic.repository.InstitutionRepository;
import com.PEWUE.medical_clinic.validator.InstitutionValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class InstitutionService {
    private final InstitutionRepository institutionRepository;
    private final DoctorRepository doctorRepository;

    public Page<Institution> find(Pageable pageable) {
        log.info("Finding institution with pageable={}", pageable);
        return institutionRepository.findAll(pageable);
    }

    @Transactional
    public Institution add(Institution institution) {
        log.info("Adding institution {}", institution.getName());
        InstitutionValidator.validateCreateInstitution(institution, institutionRepository);
        log.info("Successfully added institution {}", institution.getName());
        return institutionRepository.save(institution);
    }

    @Transactional
    public void delete(Long id) {
        log.info("Deleting institution with id {}", id);
        Institution institution = institutionRepository.findById(id)
                .orElseThrow(() -> new InstitutionNotFoundException(id));
        institutionRepository.delete(institution);
        log.info("Institution with id {} was deleted", id);
    }

    @Transactional
    public Institution assignDoctorToInstitution(Long doctorId, Long institutionId) {
        log.info("Assigning doctorId {} to institutionId {}", doctorId, institutionId);
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new DoctorNotFoundException(doctorId));
        Institution institution = institutionRepository.findById(institutionId)
                .orElseThrow(() -> new InstitutionNotFoundException(institutionId));
        institution.getDoctors().add(doctor);
        log.info("DoctorId {} successfully assigned to institutionId {}", doctorId, institutionId);
        return institutionRepository.save(institution);
    }
}
