package com.PEWUE.medical_clinic.repository;

import com.PEWUE.medical_clinic.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findByUserEmail(String email);
    Optional<Patient> findByIdCardNo(String idCardNo);
}
