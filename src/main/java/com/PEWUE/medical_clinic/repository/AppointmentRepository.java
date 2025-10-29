package com.PEWUE.medical_clinic.repository;

import com.PEWUE.medical_clinic.model.Appointment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    @Query("""
            select a from Appointment a
            left join a.doctor d
            left join a.patient p
            where (:doctorId is null or d.id = :doctorId)
            and (:patientId is null or p.id = :patientId)
            """)
    Page<Appointment> findByFilters(Long doctorId, Long patientId, Pageable pageable);

    @Query("""
            select v from Appointment v
            where v.doctor.id = :doctorId
            and v.startTime < :endTime
            and v.endTime > :startTime
            """)
    List<Appointment> existsByDoctorIdAndTimeRange(@Param("doctorId") Long doctorId,
                                                   @Param("startTime") LocalDateTime start,
                                                   @Param("endTime") LocalDateTime end);

    @Query("""
            select a from Appointment a
            left join a.doctor d
            left join a.patient p
            where p.id = :patientId
              and (:specialization is null or d.specialization = :specialization)
              and a.startTime between :from and :to
            """)
    Page<Appointment> findByPatientAndSpecializationAndDateRange(
            Long patientId,
            String specialization,
            LocalDateTime from,
            LocalDateTime to,
            Pageable pageable
    );

    @Query("""
            select a from Appointment a
            left join a.doctor d
            where d.id = :doctorId
              and a.patient is null
              and a.startTime > :now
            """)
    Page<Appointment> findFreeAppointmentsFromNow(Long doctorId, LocalDateTime now, Pageable pageable);

    @Query("""
            select a from Appointment a
            left join a.doctor d
            where a.startTime >= :from
              and a.startTime <= :to
              and a.patient is null
              and (:specialization is null or d.specialization = :specialization)
            """)
    Page<Appointment> findFreeSlotsBySpecializationAndDateRange(
            String specialization,
            LocalDateTime from,
            LocalDateTime to,
            Pageable pageable
    );

    @Query("""
            select a from Appointment a
            join a.doctor d
            where d.specialization = :specialization
              and a.patient is null
              and a.startTime >= :startOfDay
              and a.startTime < :endOfDay
            """)
    Page<Appointment> findFreeAppointmentsBySpecializationAndDay(
            String specialization,
            LocalDateTime startOfDay,
            LocalDateTime endOfDay,
            Pageable pageable);
}