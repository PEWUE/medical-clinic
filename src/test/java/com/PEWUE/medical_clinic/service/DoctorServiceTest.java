package com.PEWUE.medical_clinic.service;

import com.PEWUE.medical_clinic.model.Doctor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.PEWUE.medical_clinic.model.User;
import com.PEWUE.medical_clinic.repository.DoctorRepository;
import com.PEWUE.medical_clinic.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DoctorServiceTest {
    DoctorRepository doctorRepository;
    UserRepository userRepository;
    DoctorService doctorService;

    @BeforeEach
    void setup() {
        this.doctorRepository = mock(DoctorRepository.class);
        this.userRepository = mock(UserRepository.class);
        this.doctorService = new DoctorService(doctorRepository, userRepository);
    }

    @Test
    void getDoctors_DataCorrect_ListDoctorReturned() {
        //given
        Pageable pageable = PageRequest.of(0, 3);
        List<Doctor> doctors = List.of(
                Doctor.builder().id(1L).firstName("name1").lastName("lastname1").specialization("surgeon").build(),
                Doctor.builder().id(2L).firstName("name2").lastName("lastname2").specialization("gynecologist").build(),
                Doctor.builder().id(3L).firstName("name3").lastName("lastname3").specialization("dentist").build()
        );
        Page<Doctor> expectedPage = new PageImpl<>(doctors, pageable, doctors.size());

        when(doctorRepository.findAll(pageable)).thenReturn(expectedPage);

        //when
        Page<Doctor> result = doctorService.find(pageable);

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
                () -> assertEquals("surgeon", result.getContent().get(0).getSpecialization()),
                () -> assertEquals("gynecologist", result.getContent().get(1).getSpecialization()),
                () -> assertEquals("dentist", result.getContent().get(2).getSpecialization())
        );
        verify(doctorRepository).findAll(pageable);
    }

    @Test
    void getDoctorByEmail_DataCorrect_DoctorReturned() {
        //given
        User expectedUser = User.builder()
                .email("useremail@example.com")
                .username("username")
                .build();
        Doctor expectedDoctor = Doctor.builder()
                .firstName("John")
                .lastName("Smith")
                .specialization("gynecologist")
                .user(expectedUser)
                .build();

        when(doctorRepository.findByUserEmail("useremail@example.com")).thenReturn(Optional.of(expectedDoctor));

        //when
        Doctor result = doctorService.find("useremail@example.com");

        //then
        assertAll(
                () -> assertEquals("useremail@example.com", result.getUser().getEmail()),
                () -> assertEquals("username", result.getUser().getUsername()),
                () -> assertEquals("John", result.getFirstName()),
                () -> assertEquals("Smith", result.getLastName()),
                () -> assertEquals("gynecologist", result.getSpecialization())
        );
        verify(doctorRepository).findByUserEmail("useremail@example.com");
    }

    @Test
    void addDoctor_DataCorrect_DoctorReturned() {
        //given
        User inputUser = User.builder()
                .email("useremail@example.com")
                .username("username1999")
                .password("password123!")
                .build();
        Doctor inputDoctor = Doctor.builder()
                .firstName("name")
                .lastName("lastname")
                .specialization("surgeon")
                .user(inputUser)
                .build();
        User user = User.builder()
                .id(1L)
                .email("useremail@example.com")
                .username("username1999")
                .password("password123!")
                .build();
        Doctor doctor = Doctor.builder()
                .id(1L)
                .firstName("name")
                .lastName("lastname")
                .specialization("surgeon")
                .user(user)
                .institutions(new ArrayList<>())
                .appointments(new ArrayList<>())
                .build();

        when(doctorRepository.save(inputDoctor)).thenReturn(doctor);

        //when
        Doctor result = doctorService.add(inputDoctor);

        //then
        assertAll(
                () -> assertNotNull(result.getId()),
                () -> assertEquals(1L, result.getId()),
                () -> assertEquals("name", result.getFirstName()),
                () -> assertEquals("lastname", result.getLastName()),
                () -> assertEquals("surgeon", result.getSpecialization()),
                () -> assertTrue(result.getAppointments().isEmpty()),
                () -> assertTrue(result.getInstitutions().isEmpty()),
                () -> assertNotNull(result.getUser().getId()),
                () -> assertEquals(1L, result.getUser().getId()),
                () -> assertEquals("useremail@example.com", result.getUser().getEmail()),
                () -> assertEquals("username1999", result.getUser().getUsername())
        );
        verify(doctorRepository).save(inputDoctor);
    }

    @Test
    void deleteDoctor_EmailProvided_RepositoryDeleteCalled() {
        //given
        String email = "doctor@clinic.com";
        User user = User.builder()
                .email(email)
                .build();
        Doctor foundDoctor = Doctor.builder()
                .user(user)
                .build();

        when(doctorRepository.findByUserEmail(email)).thenReturn(Optional.of(foundDoctor));

        //when
        doctorService.delete(email);

        //then
        verify(doctorRepository).delete(foundDoctor);
    }

    @Test
    void editDoctor_EmailProvided_DoctorReturned() {
        //given
        String email = "johny@example.com";
        User user = User.builder()
                .email(email)
                .build();
        Doctor foundDoctor = Doctor.builder()
                .id(10L)
                .firstName("John")
                .lastName("Smith")
                .specialization("surgeon")
                .user(user)
                .build();
        Doctor updatedDoctor = Doctor.builder()
                .firstName("Will")
                .lastName("Jones")
                .specialization("gynecologist")
                .build();

        when(doctorRepository.findByUserEmail(email)).thenReturn(Optional.of(foundDoctor));
        when(doctorRepository.save(foundDoctor)).thenReturn(foundDoctor);

        //when
        Doctor returnedDoctor = doctorService.edit(email, updatedDoctor);

        //then
        assertAll(
                () -> assertEquals("Will", returnedDoctor.getFirstName()),
                () -> assertEquals("Jones", returnedDoctor.getLastName()),
                () -> assertEquals("gynecologist", returnedDoctor.getSpecialization())
        );
        verify(doctorRepository).save(foundDoctor);
    }
}
