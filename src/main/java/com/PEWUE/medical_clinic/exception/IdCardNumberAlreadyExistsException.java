package com.PEWUE.medical_clinic.exception;

import org.springframework.http.HttpStatus;

public class IdCardNumberAlreadyExistsException extends MedClinicException {

    public IdCardNumberAlreadyExistsException(String idCardNo) {
        super("ID card number: " + idCardNo + " already exists", HttpStatus.CONFLICT);
    }
}
