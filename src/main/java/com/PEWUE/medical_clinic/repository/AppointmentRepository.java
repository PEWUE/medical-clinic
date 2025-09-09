package com.PEWUE.medical_clinic.repository;

import com.PEWUE.medical_clinic.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByDoctorId(Long doctorId);

    List<Appointment> findByPatientId(Long patientId);

    List<Appointment> findByDoctorIdAndPatientId(Long doctorId, Long patientId);

    @Query("""
            select v from Appointment v
            where v.doctor.id = :doctorId
            and v.startTime < :endTime
            and v.endTime > :startTime
            """)
    List<Appointment> existsByDoctorIdAndTimeRange(@Param("doctorId") Long doctorId,
                                         @Param("startTime") LocalDateTime start,
                                         @Param("endTime") LocalDateTime end);
}
