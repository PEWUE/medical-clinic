package com.PEWUE.medical_clinic.mapper;

import com.PEWUE.medical_clinic.dto.PageDto;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

import java.util.function.Function;

@Mapper(componentModel = "spring")
public interface PageMapper {
    default <T, D> PageDto<D> toDto(Page<T> page, Function<T, D> mapper) {
        var content = page.getContent().stream()
                .map(mapper)
                .toList();

        return new PageDto<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }
}
