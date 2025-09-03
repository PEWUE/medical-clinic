package com.PEWUE.medical_clinic.mapper;

import com.PEWUE.medical_clinic.command.InstitutionCreateCommand;
import com.PEWUE.medical_clinic.dto.InstitutionDto;
import com.PEWUE.medical_clinic.model.Doctor;
import com.PEWUE.medical_clinic.model.Institution;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface InstitutionMapper {
    @Mapping(source = "doctors", target = "doctorsIds", qualifiedByName = "doctorsToIds")
    InstitutionDto toDto(Institution institution);
    Institution toEntity(InstitutionCreateCommand command);

    @Named("doctorsToIds")
    default List<Long> doctorsToIds(List<Doctor> doctors) {
        return doctors.stream()
                .map(Doctor::getId)
                .toList();
    }
}
