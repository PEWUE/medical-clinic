package com.PEWUE.medical_clinic.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Patient {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String idCardNo;
    private String phoneNumber;
    private LocalDate birthday;
}
