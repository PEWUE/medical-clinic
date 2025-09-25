package com.PEWUE.medical_clinic.service;

import com.PEWUE.medical_clinic.exception.DoctorNotFoundException;
import com.PEWUE.medical_clinic.exception.EmailAlreadyExistsException;
import com.PEWUE.medical_clinic.exception.FieldsShouldNotBeNullException;
import com.PEWUE.medical_clinic.exception.UserNotFoundException;
import com.PEWUE.medical_clinic.exception.UsernameAlreadyExistsException;
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
import org.springframework.http.HttpStatus;

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
    void find_DataCorrect_DoctorsReturned() {
        //given
        Pageable pageable = PageRequest.of(0, 3);
        List<Doctor> doctors = List.of(
                Doctor.builder().id(1L).firstName("name1").lastName("lastname1").specialization("surgeon").build(),
                Doctor.builder().id(2L).firstName("name2").lastName("lastname2").specialization("gynecologist").build(),
                Doctor.builder().id(3L).firstName("name3").lastName("lastname3").specialization("dentist").build()
        );
        Page<Doctor> page = new PageImpl<>(doctors, pageable, doctors.size());

        when(doctorRepository.findAll(pageable)).thenReturn(page);

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
    void find_DataCorrect_DoctorReturned() {
        //given
        User user = User.builder()
                .email("useremail@example.com")
                .username("username")
                .build();
        Doctor doctor = Doctor.builder()
                .firstName("John")
                .lastName("Smith")
                .specialization("gynecologist")
                .user(user)
                .build();

        when(doctorRepository.findByUserEmail("useremail@example.com")).thenReturn(Optional.of(doctor));

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
    void find_DoctorNotFound_DoctorNotFoundExceptionThrown() {
        //given
        String email = "doctor@email.com";

        when(doctorRepository.findByUserEmail(email)).thenReturn(Optional.empty());

        //when
        DoctorNotFoundException exception = assertThrows(DoctorNotFoundException.class,
                () -> doctorService.find(email));

        //then
        assertAll(
                () -> assertEquals("Doctor with email doctor@email.com not found", exception.getMessage()),
                () -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus())
        );
    }

    @Test
    void add_DataCorrect_DoctorReturned() {
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
    void add_DoctorUserFieldIsNull_FieldsShouldNotBeNullExceptionThrown() {
        //given
        Doctor doctor = Doctor.builder()
                .firstName("name")
                .lastName("lastname")
                .specialization("surgeon")
                .build();

        //when
        FieldsShouldNotBeNullException exception = assertThrows(FieldsShouldNotBeNullException.class,
                () -> doctorService.add(doctor));

        //then
        assertAll(
                () -> assertEquals("Fields should not be null", exception.getMessage()),
                () -> assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus())
        );
    }

    @Test
    void add_GivenUserNotFound_UserNotFoundExceptionThrown() {
        //given
        Doctor doctor = Doctor.builder()
                .firstName("name")
                .lastName("lastname")
                .specialization("surgeon")
                .user(User.builder().id(4L).build())
                .build();

        when(userRepository.findById(doctor.getUser().getId())).thenReturn(Optional.empty());

        //when
        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> doctorService.add(doctor));

        //then
        assertAll(
                () -> assertEquals("User with id 4 not found", exception.getMessage()),
                () -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus())
        );
    }

    @Test
    void add_DoctorEmailFieldIsNull_FieldsShouldNotBeNullExceptionThrown() {
        //given
        Doctor doctor = Doctor.builder()
                .firstName("name")
                .lastName("lastname")
                .specialization("surgeon")
                .user(User.builder().username("username").password("password").build())
                .build();

        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        //when
        FieldsShouldNotBeNullException exception = assertThrows(FieldsShouldNotBeNullException.class,
                () -> doctorService.add(doctor));

        //then
        assertAll(
                () -> assertEquals("Fields should not be null", exception.getMessage()),
                () -> assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus())
        );
    }

    @Test
    void add_GivenEmailAlreadyExists_EmailAlreadyExistsExceptionThrown() {
        //given
        Doctor doctor = Doctor.builder()
                .firstName("name")
                .lastName("lastname")
                .specialization("surgeon")
                .user(User.builder().username("username").password("password").email("doctor@email.com").build())
                .build();

        when(userRepository.findByEmail(doctor.getUser().getEmail())).thenReturn(Optional.of(doctor.getUser()));

        //when
        EmailAlreadyExistsException exception = assertThrows(EmailAlreadyExistsException.class,
                () -> doctorService.add(doctor));

        //then
        assertAll(
                () -> assertEquals("Email doctor@email.com is already taken", exception.getMessage()),
                () -> assertEquals(HttpStatus.CONFLICT, exception.getStatus())
        );
    }

    @Test
    void add_GivenUsernameAlreadyExists_EmailAlreadyExistsExceptionThrown() {
        //given
        Doctor doctor = Doctor.builder()
                .firstName("name")
                .lastName("lastname")
                .specialization("surgeon")
                .user(User.builder().username("username").password("password").email("doctor@email.com").build())
                .build();

        when(userRepository.findByUsername(doctor.getUser().getUsername())).thenReturn(Optional.of(doctor.getUser()));

        //when
        UsernameAlreadyExistsException exception = assertThrows(UsernameAlreadyExistsException.class,
                () -> doctorService.add(doctor));

        //then
        assertAll(
                () -> assertEquals("Username username is already taken", exception.getMessage()),
                () -> assertEquals(HttpStatus.CONFLICT, exception.getStatus())
        );
    }

    @Test
    void delete_DataCorrect_RepositoryDeleteCalled() {
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
    void delete_DoctorNotFound_DoctorNotFoundExceptionThrown() {
        //given
        String email = "johny.silver@email.com";

        when(doctorRepository.findByUserEmail(email)).thenReturn(Optional.empty());

        //when
        DoctorNotFoundException exception = assertThrows(DoctorNotFoundException.class,
                () -> doctorService.delete(email));

        //then
        assertAll(
                () -> assertEquals("Doctor with email johny.silver@email.com not found", exception.getMessage()),
                () -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus())
        );
    }

    @Test
    void editDoctor_EmailProvided_DoctorReturned() {
        //given
        String email = "johny@example.com";
        User user = User.builder()
                .id(5L)
                .email(email)
                .username("username")
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
                () -> assertEquals(5L, returnedDoctor.getUser().getId()),
                () -> assertEquals("johny@example.com", returnedDoctor.getUser().getEmail()),
                () -> assertEquals("username", returnedDoctor.getUser().getUsername()),
                () -> assertEquals(10L, returnedDoctor.getId()),
                () -> assertEquals("Will", returnedDoctor.getFirstName()),
                () -> assertEquals("Jones", returnedDoctor.getLastName()),
                () -> assertEquals("gynecologist", returnedDoctor.getSpecialization())
        );
        verify(doctorRepository).save(foundDoctor);
    }

    @Test
    void edit_DoctorNotFound_DoctorNotFoundExceptionThrown() {
        //given
        String email = "doctor@clinic.com";
        Doctor doctor = Doctor.builder().id(1L).build();

        when(doctorRepository.findByUserEmail(email)).thenReturn(Optional.empty());

        //when
        DoctorNotFoundException exception = assertThrows(DoctorNotFoundException.class,
                () -> doctorService.edit(email, doctor));

        //then
        assertAll(
                () -> assertEquals("Doctor with email doctor@clinic.com not found", exception.getMessage()),
                () -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus())
        );
    }

    @Test
    void edit_DoctorSpecializationFieldIsNull_DoctorNotFoundExceptionThrown() {
        //given
        String email = "doctor@clinic.com";
        Doctor doctor = Doctor.builder().id(1L).firstName("John").lastName("Smith").build();

        when(doctorRepository.findByUserEmail(email)).thenReturn(Optional.of(doctor));

        //when
        FieldsShouldNotBeNullException exception = assertThrows(FieldsShouldNotBeNullException.class,
                () -> doctorService.edit(email, doctor));

        //then
        assertAll(
                () -> assertEquals("Fields should not be null", exception.getMessage()),
                () -> assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus())
        );
    }
}