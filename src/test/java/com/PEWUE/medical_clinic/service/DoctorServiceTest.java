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
        Pageable pageable = PageRequest.of(0,3);
        List<Doctor> doctors = List.of(new Doctor(), new Doctor(), new Doctor());
        Page<Doctor> expectedPage = new PageImpl<>(doctors, pageable, doctors.size());

        when(doctorRepository.findAll(pageable)).thenReturn(expectedPage);

        //when
        Page<Doctor> result = doctorService.find(pageable);

        //then
        assertEquals(expectedPage, result);
        assertEquals(3, result.getContent().size());
        verify(doctorRepository).findAll(pageable);
    }

    @Test
    void getDoctorByEmail_DataCorrect_DoctorReturned() {
        //given
        User expectedUser = User.builder()
                .email("useremail@example.com")
                .build();
        Doctor expectedDoctor = Doctor.builder()
                .user(expectedUser)
                .build();

        when(doctorRepository.findByUserEmail("useremail@example.com")).thenReturn(Optional.of(expectedDoctor));

        //when
        Doctor result = doctorService.find("useremail@example.com");

        //then
        assertEquals(expectedDoctor.getUser().getEmail(), result.getUser().getEmail());
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
        User expectedUser = User.builder()
                .id(1L)
                .email("useremail@example.com")
                .username("username1999")
                .password("password123!")
                .build();
        Doctor expectedDoctor = Doctor.builder()
                .id(1L)
                .firstName("name")
                .lastName("lastname")
                .specialization("surgeon")
                .user(expectedUser)
                .institutions(new ArrayList<>())
                .appointments(new ArrayList<>())
                .build();

        when(doctorRepository.save(inputDoctor)).thenReturn(expectedDoctor);

        //when
        Doctor result = doctorService.add(inputDoctor);

        //then
        assertNotNull(result.getId());
        assertEquals(expectedDoctor.getId(), result.getId());
        assertEquals(expectedDoctor.getFirstName(), result.getFirstName());
        assertEquals(expectedDoctor.getLastName(), result.getLastName());
        assertEquals(expectedDoctor.getSpecialization(), result.getSpecialization());
        assertTrue(result.getAppointments().isEmpty());
        assertTrue(result.getInstitutions().isEmpty());
        assertNotNull(result.getUser().getId());
        assertEquals(expectedDoctor.getUser().getId(), result.getUser().getId());
        assertEquals(expectedDoctor.getUser().getEmail(), result.getUser().getEmail());
        assertEquals(expectedDoctor.getUser().getUsername(), result.getUser().getUsername());
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
}
