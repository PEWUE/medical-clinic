package com.PEWUE.medical_clinic.exception;

import com.PEWUE.medical_clinic.dto.ErrorMessageDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = MedClinicException.class)
    protected ResponseEntity<ErrorMessageDto> handleMedClinicException(MedClinicException ex, HttpServletRequest request) {
        log.warn("Handled MedClinicException: {} at path {}", ex.getMessage(), request.getRequestURI());
        ErrorMessageDto error = ErrorMessageDto.builder()
                .timestamp(LocalDateTime.now())
                .status(ex.getStatus().value())
                .error(ex.getStatus().getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(ex.getStatus()).body(error);
    }

    @ExceptionHandler(value = Exception.class)
    protected ResponseEntity<ErrorMessageDto> handleUnexpectedException(Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception at path {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        ErrorMessageDto error = ErrorMessageDto.builder()
                .timestamp(LocalDateTime.now())
                .status(500)
                .error("Unexpected error")
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(500).body(error);
    }
}
