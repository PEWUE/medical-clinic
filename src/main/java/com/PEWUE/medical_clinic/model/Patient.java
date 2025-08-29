package com.PEWUE.medical_clinic.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "FIRST_NAME")
    private String firstName;
    @Column(name = "LAST_NAME")
    private String lastName;
    @Column(name = "EMAIL", unique = true)
    private String email;
    @Column(name = "PASSWORD")
    private String password;
    @Column(name = "ID_CARD_NO", unique = true)
    private String idCardNo;
    @Column(name = "PHONE_NUMBER")
    private String phoneNumber;
    @Column(name = "BIRTHDAY")
    private LocalDate birthday;

    public void edit(Patient newData) {
        this.firstName = newData.getFirstName();
        this.lastName = newData.getLastName();
        this.email = newData.getEmail();
        this.password = newData.getPassword();
        this.idCardNo = newData.getIdCardNo();
        this.phoneNumber = newData.getPhoneNumber();
        this.birthday = newData.getBirthday();
    }
}
