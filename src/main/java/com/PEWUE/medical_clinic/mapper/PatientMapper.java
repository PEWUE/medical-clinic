package com.PEWUE.medical_clinic.mapper;

import com.PEWUE.medical_clinic.command.PatientCreateCommand;
import com.PEWUE.medical_clinic.command.PatientEditCommand;
import com.PEWUE.medical_clinic.dto.PatientDto;
import com.PEWUE.medical_clinic.model.Appointment;
import com.PEWUE.medical_clinic.model.Patient;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PatientMapper {

    @Mapping(source = "appointments", target = "appointmentsIds", qualifiedByName = "appointmentsToIds")
    PatientDto toDto(Patient patient);

    Patient toEntity(PatientCreateCommand command);

    Patient toEntity(PatientEditCommand command);

    @Named("appointmentsToIds")
    default List<Long> appointmentsToIds(List<Appointment> appointments) {
        return appointments.stream()
                .map(Appointment::getId)
                .toList();
    }
}
