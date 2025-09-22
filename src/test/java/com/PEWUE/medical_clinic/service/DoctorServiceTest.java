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
        assertEquals(expectedDoctor, result);
        verify(doctorRepository).findByUserEmail("useremail@example.com");
    }
}
