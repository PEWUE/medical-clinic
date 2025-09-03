package com.PEWUE.medical_clinic.repository;

import com.PEWUE.medical_clinic.model.Institution;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InstitutionRepository extends JpaRepository<Institution, Long> {
}
