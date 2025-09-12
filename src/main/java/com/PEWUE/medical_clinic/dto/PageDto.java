package com.PEWUE.medical_clinic.dto;

import java.util.List;

public record PageDto<T>(
        List<T> content,
        int pageNumber,
        int pageSize,
        long totalElements,
        int totalPages
) {
}
