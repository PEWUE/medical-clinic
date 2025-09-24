package com.PEWUE.medical_clinic.service;

import com.PEWUE.medical_clinic.model.Doctor;
import com.PEWUE.medical_clinic.model.Institution;
import com.PEWUE.medical_clinic.repository.DoctorRepository;
import com.PEWUE.medical_clinic.repository.InstitutionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class InstitutionServiceTest {
    InstitutionRepository institutionRepository;
    DoctorRepository doctorRepository;
    InstitutionService institutionService;

    @BeforeEach
    void setup() {
        this.institutionRepository = mock(InstitutionRepository.class);
        this.doctorRepository = mock(DoctorRepository.class);
        this.institutionService = new InstitutionService(institutionRepository, doctorRepository);
    }

    @Test
    void getInstitutions_DataCorrect_InstitutionsReturned() {
        //given
        Pageable pageable = PageRequest.of(0, 2);
        List<Doctor> doctors = List.of(
                Doctor.builder().id(1L).firstName("name1").lastName("lastname1").specialization("surgeon").build(),
                Doctor.builder().id(2L).firstName("name2").lastName("lastname2").specialization("gynecologist").build(),
                Doctor.builder().id(3L).firstName("name3").lastName("lastname3").specialization("dentist").build()
        );
        List<Institution> institutions = List.of(
                Institution.builder()
                        .id(1L)
                        .name("Institution 1 name")
                        .city("City 1")
                        .postalCode("POSTAL-CODE1")
                        .street("Street One")
                        .streetNo("22")
                        .doctors(List.of(doctors.get(0), doctors.get(1)))
                        .build(),
                Institution.builder()
                        .id(2L)
                        .name("Institution 2 name")
                        .city("City 2")
                        .postalCode("POSTAL-CODE2")
                        .street("Street Two")
                        .streetNo("43a")
                        .doctors(List.of(doctors.get(2)))
                        .build()
        );

        Page<Institution> page = new PageImpl<>(institutions, pageable, institutions.size());

        when(institutionRepository.findAll(pageable)).thenReturn(page);

        //when
        Page<Institution> result = institutionService.find(pageable);

        //then
        assertAll(
                () -> assertEquals(2, result.getContent().size()),
                () -> assertEquals(1L, result.getContent().get(0).getId()),
                () -> assertEquals("Institution 1 name", result.getContent().get(0).getName()),
                () -> assertEquals("City 1", result.getContent().get(0).getCity()),
                () -> assertEquals("POSTAL-CODE1", result.getContent().get(0).getPostalCode()),
                () -> assertEquals("Street One", result.getContent().get(0).getStreet()),
                () -> assertEquals("22", result.getContent().get(0).getStreetNo()),
                () -> assertEquals(2, result.getContent().get(0).getDoctors().size()),
                () -> assertEquals(1L, result.getContent().get(0).getDoctors().get(0).getId()),
                () -> assertEquals("name1", result.getContent().get(0).getDoctors().get(0).getFirstName()),
                () -> assertEquals("lastname1", result.getContent().get(0).getDoctors().get(0).getLastName()),
                () -> assertEquals("surgeon", result.getContent().get(0).getDoctors().get(0).getSpecialization()),
                () -> assertEquals(2L, result.getContent().get(0).getDoctors().get(1).getId()),
                () -> assertEquals("name2", result.getContent().get(0).getDoctors().get(1).getFirstName()),
                () -> assertEquals("lastname2", result.getContent().get(0).getDoctors().get(1).getLastName()),
                () -> assertEquals("gynecologist", result.getContent().get(0).getDoctors().get(1).getSpecialization()),
                () -> assertEquals(2L, result.getContent().get(1).getId()),
                () -> assertEquals("Institution 2 name", result.getContent().get(1).getName()),
                () -> assertEquals("City 2", result.getContent().get(1).getCity()),
                () -> assertEquals("POSTAL-CODE2", result.getContent().get(1).getPostalCode()),
                () -> assertEquals("Street Two", result.getContent().get(1).getStreet()),
                () -> assertEquals("43a", result.getContent().get(1).getStreetNo()),
                () -> assertEquals(1, result.getContent().get(1).getDoctors().size()),
                () -> assertEquals(3L, result.getContent().get(1).getDoctors().get(0).getId()),
                () -> assertEquals("name3", result.getContent().get(1).getDoctors().get(0).getFirstName()),
                () -> assertEquals("lastname3", result.getContent().get(1).getDoctors().get(0).getLastName()),
                () -> assertEquals("dentist", result.getContent().get(1).getDoctors().get(0).getSpecialization())
        );
        verify(institutionRepository).findAll(pageable);
    }

    @Test
    void addInstitution_DataCorrect_InstitutionReturned() {
        //given
        Institution inputInstitution = Institution.builder()
                .name("Institution 2 name")
                .city("City 2")
                .postalCode("POSTAL-CODE2")
                .street("Street Two")
                .streetNo("43a")
                .doctors(new ArrayList<>())
                .build();

        Institution institution = Institution.builder()
                .id(2L)
                .name("Institution 2 name")
                .city("City 2")
                .postalCode("POSTAL-CODE2")
                .street("Street Two")
                .streetNo("43a")
                .doctors(new ArrayList<>())
                .build();

        when(institutionRepository.save(inputInstitution)).thenReturn(institution);

        //when
        Institution result = institutionService.add(inputInstitution);

        //then
        assertAll(
                () -> assertEquals(2L, result.getId()),
                () -> assertEquals("Institution 2 name", result.getName()),
                () -> assertEquals("City 2", result.getCity()),
                () -> assertEquals("POSTAL-CODE2", result.getPostalCode()),
                () -> assertEquals("Street Two", result.getStreet()),
                () -> assertEquals("43a", result.getStreetNo()),
                () -> assertTrue(result.getDoctors().isEmpty())
        );
        verify(institutionRepository).save(inputInstitution);
    }

    @Test
    void deleteInstitution_DataCorrect_RepositoryDeleteCalled() {
        //given
        Long id = 2L;
        Institution institution = Institution.builder()
                .id(2L)
                .name("Institution 2 name")
                .city("City 2")
                .postalCode("POSTAL-CODE2")
                .street("Street Two")
                .streetNo("43a")
                .doctors(new ArrayList<>())
                .build();

        when(institutionRepository.findById(id)).thenReturn(Optional.of(institution));

        //when
        institutionService.delete(id);

        //then
        verify(institutionRepository).delete(institution);
    }

    @Test
    void assignDoctorToInstitution_DataCorrect_InstitutionReturned() {
        //given
        Long doctorId = 6L;
        Long institutionId = 4L;
        Doctor doctor = Doctor.builder()
                .id(6L)
                .firstName("name6")
                .lastName("lastname6")
                .specialization("surgeon")
                .institutions(new ArrayList<>())
                .build();
        Institution institution = Institution.builder()
                .id(4L)
                .name("Institution 4 name")
                .city("City 4")
                .postalCode("POSTAL-CODE4")
                .street("Street Four")
                .streetNo("56f")
                .doctors(new ArrayList<>())
                .build();

        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(institutionRepository.findById(institutionId)).thenReturn(Optional.of(institution));
        when(institutionRepository.save(institution)).thenAnswer(invocation -> invocation.getArgument(0));

        //when
        Institution result = institutionService.assignDoctorToInstitution(doctorId, institutionId);

        //then
        assertAll(
                () -> assertEquals(4L, result.getId()),
                () -> assertEquals("Institution 4 name", result.getName()),
                () -> assertEquals("City 4", result.getCity()),
                () -> assertEquals("POSTAL-CODE4", result.getPostalCode()),
                () -> assertEquals("Street Four", result.getStreet()),
                () -> assertEquals("56f", result.getStreetNo()),
                () -> assertTrue(result.getDoctors().contains(doctor)),
                () -> assertEquals(6L, result.getDoctors().get(0).getId()),
                () -> assertEquals("name6", result.getDoctors().get(0).getFirstName()),
                () -> assertEquals("lastname6", result.getDoctors().get(0).getLastName()),
                () -> assertEquals("surgeon", result.getDoctors().get(0).getSpecialization())
        );
        verify(doctorRepository).findById(doctorId);
        verify(institutionRepository).findById(institutionId);
        verify(institutionRepository).save(institution);
    }
}