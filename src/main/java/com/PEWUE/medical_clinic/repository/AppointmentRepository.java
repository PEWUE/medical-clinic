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

    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Appointment a " +
            "WHERE a.doctor.id = :doctorId AND " +
            "((:start BETWEEN a.startTime AND a.endTime) OR " +
            "(:end BETWEEN a.startTime AND a.endTime) OR " +
            "(a.startTime BETWEEN :start AND :end) OR " +
            "(a.endTime BETWEEN :start AND :end))")
    boolean existsByDoctorIdAndTimeRange(@Param("doctorId") Long doctorId,
                                         @Param("start") LocalDateTime start,
                                         @Param("end") LocalDateTime end);
}
