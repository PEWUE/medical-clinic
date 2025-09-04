package com.PEWUE.medical_clinic.exception;

import org.springframework.http.HttpStatus;

public class IdCardNumberAlreadyExists extends MedClinicException {

    public IdCardNumberAlreadyExists(String idCardNo) {
        super("ID card number: " + idCardNo + "already exists", HttpStatus.CONFLICT);
    }
}
