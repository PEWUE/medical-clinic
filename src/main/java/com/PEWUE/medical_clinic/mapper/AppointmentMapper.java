package com.PEWUE.medical_clinic.mapper;

import com.PEWUE.medical_clinic.dto.AppointmentDto;
import com.PEWUE.medical_clinic.model.Appointment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AppointmentMapper {

    @Mapping(source = "doctor.id", target = "doctorId")
    @Mapping(source = "patient.id", target = "patientId")
    AppointmentDto toDto(Appointment appointment);
}
