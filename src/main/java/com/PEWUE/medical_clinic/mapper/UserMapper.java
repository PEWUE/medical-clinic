package com.PEWUE.medical_clinic.mapper;

import com.PEWUE.medical_clinic.command.UserCreateCommand;
import com.PEWUE.medical_clinic.dto.UserDto;
import com.PEWUE.medical_clinic.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);
    User toEntity(UserCreateCommand command);
}
