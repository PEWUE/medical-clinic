package com.PEWUE.medical_clinic.mapper;

import com.PEWUE.medical_clinic.command.DoctorCreateCommand;
import com.PEWUE.medical_clinic.command.DoctorEditCommand;
import com.PEWUE.medical_clinic.dto.DoctorDto;
import com.PEWUE.medical_clinic.model.Doctor;
import com.PEWUE.medical_clinic.model.Institution;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DoctorMapper {

    @Mapping(source = "institutions", target = "institutionsIds", qualifiedByName = "institutionsToIds")
    DoctorDto toDto(Doctor doctor);

    Doctor toEntity(DoctorCreateCommand command);

    Doctor toEntity(DoctorEditCommand command);

    @Named("institutionsToIds")
    default List<Long> institutionsToIds(List<Institution> institutions) {
        return institutions.stream()
                .map(Institution::getId)
                .toList();
    }
 }
