package com.PEWUE.medical_clinic.repository;

import com.PEWUE.medical_clinic.model.Institution;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InstitutionRepository extends JpaRepository<Institution, Long> {
    Optional<Institution> findByName(String name);
}
