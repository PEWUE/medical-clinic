package com.PEWUE.medical_clinic.service;

import com.PEWUE.medical_clinic.command.AppointmentCreateCommand;
import com.PEWUE.medical_clinic.command.BookAppointmentCommand;
import com.PEWUE.medical_clinic.model.Appointment;
import com.PEWUE.medical_clinic.model.Doctor;
import com.PEWUE.medical_clinic.model.Patient;
import com.PEWUE.medical_clinic.repository.AppointmentRepository;
import com.PEWUE.medical_clinic.repository.DoctorRepository;
import com.PEWUE.medical_clinic.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class AppointmentServiceTest {
    AppointmentRepository appointmentRepository;
    DoctorRepository doctorRepository;
    PatientRepository patientRepository;
    AppointmentService appointmentService;

    @BeforeEach
    void setup() {
        this.appointmentRepository = mock(AppointmentRepository.class);
        this.doctorRepository = mock(DoctorRepository.class);
        this.patientRepository = mock(PatientRepository.class);
        this.appointmentService = new AppointmentService(appointmentRepository, doctorRepository, patientRepository);
    }

    @Test
    void getAppointments_DataCorrect_AppointmentsReturned() {
        // given
        Doctor doctor = Doctor.builder().id(1L).firstName("doctor1").lastName("lastname1").specialization("surgeon").build();
        Patient patient = Patient.builder().id(2L).firstName("patient1").lastName("lastname2").build();
        Pageable pageable = PageRequest.of(0, 2);
        List<Appointment> appointments = List.of(
                Appointment.builder()
                        .id(1L)
                        .doctor(doctor)
                        .patient(patient)
                        .startTime(LocalDateTime.of(2025, 10, 1, 10, 45))
                        .endTime(LocalDateTime.of(2025, 10, 1, 11, 15))
                        .build(),
                Appointment.builder().
                        id(2L)
                        .doctor(doctor)
                        .patient(patient)
                        .startTime(LocalDateTime.of(2025, 10, 1, 12, 45))
                        .endTime(LocalDateTime.of(2025, 10, 1, 13, 15))
                        .build()
        );
        Page<Appointment> page = new PageImpl<>(appointments, pageable, appointments.size());

        when(appointmentRepository.findByFilters(doctor.getId(), patient.getId(), pageable)).thenReturn(page);

        //when
        Page<Appointment> result = appointmentService.find(doctor.getId(), patient.getId(), pageable);

        //then
        assertAll(
                () -> assertEquals(2, result.getContent().size()),
                () -> assertEquals(1L, result.getContent().get(0).getId()),
                () -> assertEquals(2L, result.getContent().get(1).getId()),
                () -> assertEquals(LocalDateTime.of(2025, 10, 1, 10, 45), result.getContent().get(0).getStartTime()),
                () -> assertEquals(LocalDateTime.of(2025, 10, 1, 11, 15), result.getContent().get(0).getEndTime()),
                () -> assertEquals(LocalDateTime.of(2025, 10, 1, 12, 45), result.getContent().get(1).getStartTime()),
                () -> assertEquals(LocalDateTime.of(2025, 10, 1, 13, 15), result.getContent().get(1).getEndTime()),
                () -> assertEquals("doctor1", result.getContent().get(0).getDoctor().getFirstName()),
                () -> assertEquals("lastname1", result.getContent().get(0).getDoctor().getLastName()),
                () -> assertEquals("surgeon", result.getContent().get(0).getDoctor().getSpecialization()),
                () -> assertEquals("doctor1", result.getContent().get(1).getDoctor().getFirstName()),
                () -> assertEquals("lastname1", result.getContent().get(1).getDoctor().getLastName()),
                () -> assertEquals("surgeon", result.getContent().get(1).getDoctor().getSpecialization()),
                () -> assertEquals("patient1", result.getContent().get(0).getPatient().getFirstName()),
                () -> assertEquals("lastname2", result.getContent().get(0).getPatient().getLastName()),
                () -> assertEquals("patient1", result.getContent().get(1).getPatient().getFirstName()),
                () -> assertEquals("lastname2", result.getContent().get(1).getPatient().getLastName())
        );
    }

    @Test
    void addAppointment_DataCorrect_AppointmentReturned() {
        //given
        AppointmentCreateCommand command = new AppointmentCreateCommand(
                2L,
                LocalDateTime.of(2025, 10, 1, 10, 45),
                LocalDateTime.of(2025, 10, 1, 11, 45)
        );
        Doctor doctor = Doctor.builder().id(2L).firstName("John").lastName("Doctor").specialization("dentist").build();
        Appointment appointment = Appointment.builder()
                .id(1L)
                .doctor(doctor)
                .patient(null)
                .startTime(command.startTime())
                .endTime(command.endTime())
                .build();

        when(doctorRepository.findById(command.doctorId())).thenReturn(Optional.of(doctor));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);

        //when
        Appointment result = appointmentService.add(command);

        //then
        assertAll(
                () -> assertEquals(1L, result.getId()),
                () -> assertEquals(2L, result.getDoctor().getId()),
                () -> assertEquals("John", result.getDoctor().getFirstName()),
                () -> assertEquals("Doctor", result.getDoctor().getLastName()),
                () -> assertEquals("dentist", result.getDoctor().getSpecialization()),
                () -> assertEquals(LocalDateTime.of(2025, 10, 1, 10, 45), result.getStartTime()),
                () -> assertEquals(LocalDateTime.of(2025, 10, 1, 11, 45), result.getEndTime())
        );
        verify(doctorRepository).findById(command.doctorId());
    }

    @Test
    void bookAppointment_DataCorrect_AppointmentReturned() {
        //given
        BookAppointmentCommand command = new BookAppointmentCommand(1L, 2L);
        Doctor doctor = Doctor.builder().id(2L).firstName("John").lastName("Doctor").specialization("dentist").build();
        Appointment foundAppointment = Appointment.builder()
                .id(1L)
                .doctor(doctor)
                .patient(null)
                .startTime(LocalDateTime.of(2025, 10, 1, 10, 45))
                .endTime(LocalDateTime.of(2025, 10, 1, 11, 45))
                .build();
        Patient foundPatient = Patient.builder()
                .id(2L)
                .firstName("name1")
                .lastName("lastname1")
                .idCardNo("XYZ123")
                .phoneNumber("123-456-789")
                .birthday(LocalDate.of(1995, 5, 5))
                .build();

        when(appointmentRepository.findById(command.appointmentId())).thenReturn(Optional.of(foundAppointment));
        when(patientRepository.findById(command.patientId())).thenReturn(Optional.of(foundPatient));
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        //when
        Appointment result = appointmentService.book(command);

        //then
        assertAll(
                () -> assertEquals(1L, result.getId()),
                () -> assertEquals(2L, result.getDoctor().getId()),
                () -> assertEquals("John", result.getDoctor().getFirstName()),
                () -> assertEquals("Doctor", result.getDoctor().getLastName()),
                () -> assertEquals("dentist", result.getDoctor().getSpecialization()),
                () -> assertEquals(2L, result.getPatient().getId()),
                () -> assertEquals("name1", result.getPatient().getFirstName()),
                () -> assertEquals("lastname1", result.getPatient().getLastName()),
                () -> assertEquals("XYZ123", result.getPatient().getIdCardNo()),
                () -> assertEquals("123-456-789", result.getPatient().getPhoneNumber()),
                () -> assertEquals(LocalDate.of(1995, 5, 5), result.getPatient().getBirthday()),
                () -> assertEquals(LocalDateTime.of(2025, 10, 1, 10, 45), result.getStartTime()),
                () -> assertEquals(LocalDateTime.of(2025, 10, 1, 11, 45), result.getEndTime())
        );
    }
}
