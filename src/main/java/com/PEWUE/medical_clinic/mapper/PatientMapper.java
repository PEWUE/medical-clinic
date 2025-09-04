package com.PEWUE.medical_clinic.mapper;

import com.PEWUE.medical_clinic.command.PatientCreateCommand;
import com.PEWUE.medical_clinic.command.PatientEditCommand;
import com.PEWUE.medical_clinic.dto.PatientDto;
import com.PEWUE.medical_clinic.model.Patient;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PatientMapper {

    PatientDto toDto(Patient patient);

    Patient toEntity(PatientCreateCommand command);

    Patient toEntity(PatientEditCommand command);
}
