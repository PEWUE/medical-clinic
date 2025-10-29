package com.PEWUE.medical_clinic.repository;

import com.PEWUE.medical_clinic.model.Doctor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    Optional<Doctor> findByUserEmail(String email);

    @Query("""
            select d from Doctor d
            where (:specialization is null or d.specialization = :specialization)
            """)
    Page<Doctor> findByFilters(String specialization, Pageable pageable);
}
