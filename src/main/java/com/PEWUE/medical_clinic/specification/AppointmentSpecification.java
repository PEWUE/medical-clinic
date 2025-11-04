package com.PEWUE.medical_clinic.specification;

import com.PEWUE.medical_clinic.model.Appointment;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AppointmentSpecification {

    public static Specification<Appointment> build(
            Long doctorId,
            Long patientId,
            String specialization,
            LocalDateTime from,
            LocalDateTime to,
            Boolean freeSlots
    ) {
        Specification<Appointment> spec = (root, query, cb) -> null;

        if (doctorId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("doctor").get("id"), doctorId));
        }
        if (patientId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("patient").get("id"), patientId));
        }
        if (specialization != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("doctor").get("specialization"), specialization));
        }
        if (from != null && to != null) {
            spec = spec.and((root, query, cb) -> cb.and(
                    cb.greaterThanOrEqualTo(root.get("startTime"), from),
                    cb.lessThanOrEqualTo(root.get("endTime"), to)
            ));
        }
        if (freeSlots != null && freeSlots) {
            spec = spec.and((root, query, cb) -> cb.isNull(root.get("patient")));
        }

        return spec;
    }
}
