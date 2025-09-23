package com.PEWUE.medical_clinic.service;

import com.PEWUE.medical_clinic.model.Doctor;
import com.PEWUE.medical_clinic.model.Patient;
import com.PEWUE.medical_clinic.model.User;
import com.PEWUE.medical_clinic.repository.PatientRepository;
import com.PEWUE.medical_clinic.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class PatientServiceTest {
    PatientRepository patientRepository;
    UserRepository userRepository;
    PatientService patientService;

    @BeforeEach
    void setup() {
        this.patientRepository = mock(PatientRepository.class);
        this.userRepository = mock(UserRepository.class);
        this.patientService = new PatientService(patientRepository, userRepository);
    }

    @Test
    void getPatients_DataCorrect_ListPatientsReturned() {
        //given
        Pageable pageable = PageRequest.of(0, 3);
        List<Patient> patients = List.of(
                Patient.builder().id(1L).firstName("name1").lastName("lastname1").idCardNo("XYZ123").phoneNumber("123-456-789").birthday(LocalDate.of(1995, 5, 5)).build(),
                Patient.builder().id(2L).firstName("name2").lastName("lastname2").idCardNo("ABC987").phoneNumber("987-654-321").birthday(LocalDate.of(1990, 12, 30)).build(),
                Patient.builder().id(3L).firstName("name3").lastName("lastname3").idCardNo("PWE137").phoneNumber("159-753-852").birthday(LocalDate.of(2000, 1, 11)).build()
        );
        Page<Patient> page = new PageImpl<>(patients, pageable, patients.size());

        when(patientRepository.findAll(pageable)).thenReturn(page);

        //when
        Page<Patient> result = patientService.find(pageable);

        //then
        assertAll(
                () -> assertEquals(3, result.getContent().size()),
                () -> assertEquals(1L, result.getContent().get(0).getId()),
                () -> assertEquals(2L, result.getContent().get(1).getId()),
                () -> assertEquals(3L, result.getContent().get(2).getId()),
                () -> assertEquals("name1", result.getContent().get(0).getFirstName()),
                () -> assertEquals("name2", result.getContent().get(1).getFirstName()),
                () -> assertEquals("name3", result.getContent().get(2).getFirstName()),
                () -> assertEquals("lastname1", result.getContent().get(0).getLastName()),
                () -> assertEquals("lastname2", result.getContent().get(1).getLastName()),
                () -> assertEquals("lastname3", result.getContent().get(2).getLastName()),
                () -> assertEquals("XYZ123", result.getContent().get(0).getIdCardNo()),
                () -> assertEquals("ABC987", result.getContent().get(1).getIdCardNo()),
                () -> assertEquals("PWE137", result.getContent().get(2).getIdCardNo()),
                () -> assertEquals("123-456-789", result.getContent().get(0).getPhoneNumber()),
                () -> assertEquals("987-654-321", result.getContent().get(1).getPhoneNumber()),
                () -> assertEquals("159-753-852", result.getContent().get(2).getPhoneNumber()),
                () -> assertEquals(LocalDate.of(1995, 5, 5), result.getContent().get(0).getBirthday()),
                () -> assertEquals(LocalDate.of(1990, 12, 30), result.getContent().get(1).getBirthday()),
                () -> assertEquals(LocalDate.of(2000, 1, 11), result.getContent().get(2).getBirthday())
        );
        verify(patientRepository).findAll(pageable);
    }

    @Test
    void getPatientByEmail_DataCorrect_PatientReturned() {
        //given
        User user = User.builder()
                .id(8L)
                .email("useremail@example.com")
                .username("username")
                .build();
        Patient patient = Patient.builder()
                .id(5L)
                .firstName("Paul")
                .lastName("Walker")
                .idCardNo("PWE137")
                .phoneNumber("159-753-852")
                .birthday(LocalDate.of(1988, 1, 11))
                .user(user)
                .build();

        when(patientRepository.findByUserEmail("useremail@example.com")).thenReturn(Optional.of(patient));

        //when
        Patient result = patientService.find("useremail@example.com");

        //then
        assertAll(
                () -> assertEquals(8L, result.getUser().getId()),
                () -> assertEquals("useremail@example.com", result.getUser().getEmail()),
                () -> assertEquals("username", result.getUser().getUsername()),
                () -> assertEquals(5L, result.getId()),
                () -> assertEquals("Paul", result.getFirstName()),
                () -> assertEquals("Walker", result.getLastName()),
                () -> assertEquals("PWE137", result.getIdCardNo()),
                () -> assertEquals("159-753-852", result.getPhoneNumber()),
                () -> assertEquals(LocalDate.of(1988, 1, 11), result.getBirthday())
        );
        verify(patientRepository).findByUserEmail("useremail@example.com");
    }

    @Test
    void addPatient_DataCorrect_PatientReturned() {
        //given
        User inputUser = User.builder()
                .email("useremail@example.com")
                .username("username1999")
                .password("password123!")
                .build();
        Patient inputPatient = Patient.builder()
                .firstName("name")
                .lastName("lastname")
                .idCardNo("PWE137")
                .phoneNumber("159-753-852")
                .birthday(LocalDate.of(1988, 1, 11))
                .user(inputUser)
                .build();
        User user = User.builder()
                .id(1L)
                .email("useremail@example.com")
                .username("username1999")
                .password("password123!")
                .build();
        Patient patient = Patient.builder()
                .id(1L)
                .firstName("name")
                .lastName("lastname")
                .idCardNo("PWE137")
                .phoneNumber("159-753-852")
                .birthday(LocalDate.of(1988, 1, 11))
                .user(user)
                .appointments(new ArrayList<>())
                .build();

        when(patientRepository.save(inputPatient)).thenReturn(patient);

        //when
        Patient result = patientService.add(inputPatient);

        //then
        assertAll(
                () -> assertNotNull(result.getId()),
                () -> assertEquals(1L, result.getId()),
                () -> assertEquals("name", result.getFirstName()),
                () -> assertEquals("lastname", result.getLastName()),
                () -> assertEquals("PWE137", result.getIdCardNo()),
                () -> assertEquals("159-753-852", result.getPhoneNumber()),
                () -> assertEquals(LocalDate.of(1988, 1, 11), result.getBirthday()),
                () -> assertTrue(result.getAppointments().isEmpty()),
                () -> assertNotNull(result.getUser().getId()),
                () -> assertEquals(1L, result.getUser().getId()),
                () -> assertEquals("useremail@example.com", result.getUser().getEmail()),
                () -> assertEquals("username1999", result.getUser().getUsername())
        );
        verify(patientRepository).save(inputPatient);
    }

    @Test
    void deletePatient_EmailProvided_RepositoryDeleteCalled() {
        //given
        String email = "patient@clinic.com";
        User user = User.builder()
                .email(email)
                .build();
        Patient foundPatient = Patient.builder()
                .user(user)
                .build();

        when(patientRepository.findByUserEmail(email)).thenReturn(Optional.of(foundPatient));

        //when
        patientService.delete(email);

        //then
        verify(patientRepository).delete(foundPatient);
    }

    @Test
    void editPatient_EmailProvided_PatientReturned() {
        //given
        String email = "paul@example.com";
        User user = User.builder()
                .id(5L)
                .email(email)
                .username("paul99")
                .build();
        Patient foundPatient = Patient.builder()
                .id(15L)
                .firstName("Paul")
                .lastName("Jones")
                .idCardNo("PWE137")
                .phoneNumber("159-753-852")
                .birthday(LocalDate.of(1988, 1, 11))
                .user(user)
                .build();
        Patient updatedPatient = Patient.builder()
                .firstName("Will")
                .lastName("Davies")
                .phoneNumber("111-999-888")
                .birthday(LocalDate.of(1980, 10, 1))
                .build();

        when(patientRepository.findByUserEmail(email)).thenReturn(Optional.of(foundPatient));
        when(patientRepository.save(foundPatient)).thenReturn(foundPatient);

        //when
        Patient returnedPatient = patientService.edit(email, updatedPatient);

        //then
        assertAll(
                () -> assertEquals(5L, returnedPatient.getUser().getId()),
                () -> assertEquals("paul@example.com", returnedPatient.getUser().getEmail()),
                () -> assertEquals("paul99", returnedPatient.getUser().getUsername()),
                () -> assertEquals(15L, returnedPatient.getId()),
                () -> assertEquals("Will", returnedPatient.getFirstName()),
                () -> assertEquals("Davies", returnedPatient.getLastName()),
                () -> assertEquals("PWE137", returnedPatient.getIdCardNo()),
                () -> assertEquals("111-999-888", returnedPatient.getPhoneNumber()),
                () -> assertEquals(LocalDate.of(1980, 10, 1), returnedPatient.getBirthday())
        );
        verify(patientRepository).save(foundPatient);
    }
}
