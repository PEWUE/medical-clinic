package com.PEWUE.medical_clinic.service;

import com.PEWUE.medical_clinic.command.AppointmentCreateCommand;
import com.PEWUE.medical_clinic.command.BookAppointmentCommand;
import com.PEWUE.medical_clinic.exception.AppointmentAlreadyBookedException;
import com.PEWUE.medical_clinic.exception.AppointmentNotFoundException;
import com.PEWUE.medical_clinic.exception.AppointmentOverlapException;
import com.PEWUE.medical_clinic.exception.DoctorNotFoundException;
import com.PEWUE.medical_clinic.exception.InvalidAppointmentTimeException;
import com.PEWUE.medical_clinic.exception.PatientNotFoundException;
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
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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
    void find_DataCorrect_AppointmentsReturned() {
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

        when(appointmentRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

        //when
        Page<Appointment> result = appointmentService.find(doctor.getId(), patient.getId(), null, null, null, null, pageable);

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
    void add_DataCorrect_AppointmentReturned() {
        //given
        AppointmentCreateCommand command = new AppointmentCreateCommand(
                2L,
                LocalDateTime.now().plusDays(2).withHour(10).withMinute(45).withSecond(0).withNano(0),
                LocalDateTime.now().plusDays(2).withHour(11).withMinute(45).withSecond(0).withNano(0)
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
                () -> assertEquals(LocalDateTime.now().plusDays(2).withHour(10).withMinute(45).withSecond(0).withNano(0), result.getStartTime()),
                () -> assertEquals(LocalDateTime.now().plusDays(2).withHour(11).withMinute(45).withSecond(0).withNano(0), result.getEndTime())
        );
        verify(doctorRepository).findById(command.doctorId());
    }

    @Test
    void add_DoctorNotFound_DoctorNotFoundExceptionThrown() {
        //given
        AppointmentCreateCommand command = AppointmentCreateCommand.builder().doctorId(1L).build();

        when(doctorRepository.findById(command.doctorId())).thenReturn(Optional.empty());

        //when
        DoctorNotFoundException exception = assertThrows(DoctorNotFoundException.class,
                () -> appointmentService.add(command));

        //then
        assertAll(
                () -> assertEquals("Doctor with id 1 not found", exception.getMessage()),
                () -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus())
        );
    }

    @Test
    void add_StartTimeAfterEndTime_InvalidAppointmentTimeExceptionThrown() {
        //given
        AppointmentCreateCommand command = AppointmentCreateCommand.builder()
                .doctorId(1L)
                .startTime(LocalDateTime.of(2025, 10, 2, 10, 30))
                .endTime(LocalDateTime.of(2025, 10, 1, 10, 45))
                .build();
        Doctor doctor = Doctor.builder().id(1L).firstName("John").lastName("Doctor").specialization("dentist").build();

        when(doctorRepository.findById(command.doctorId())).thenReturn(Optional.of(doctor));

        //when
        InvalidAppointmentTimeException exception = assertThrows(InvalidAppointmentTimeException.class,
                () -> appointmentService.add(command));
        //then
        assertAll(
                () -> assertEquals("Appointment start time must be before the end time", exception.getMessage()),
                () -> assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus())
        );
    }

    @Test
    void add_StartTimeInPast_InvalidAppointmentTimeExceptionThrown() {
        //given
        AppointmentCreateCommand command = AppointmentCreateCommand.builder()
                .doctorId(1L)
                .startTime(LocalDateTime.now().minusHours(2))
                .endTime(LocalDateTime.now().plusHours(2))
                .build();
        Doctor doctor = Doctor.builder().id(1L).firstName("John").lastName("Doctor").specialization("dentist").build();

        when(doctorRepository.findById(command.doctorId())).thenReturn(Optional.of(doctor));

        //when
        InvalidAppointmentTimeException exception = assertThrows(InvalidAppointmentTimeException.class,
                () -> appointmentService.add(command));
        //then
        assertAll(
                () -> assertEquals("Appointment start time must be in the future", exception.getMessage()),
                () -> assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus())
        );
    }

    @Test
    void add_TimeNotMultipleOf15Minutes_InvalidAppointmentTimeExceptionThrown() {
        //given
        AppointmentCreateCommand command = AppointmentCreateCommand.builder()
                .doctorId(1L)
                .startTime(LocalDateTime.now().plusDays(2).withHour(10).withMinute(25).withSecond(0).withNano(0))
                .endTime(LocalDateTime.now().plusDays(2).withHour(10).withMinute(50).withSecond(0).withNano(0))
                .build();
        Doctor doctor = Doctor.builder().id(1L).firstName("John").lastName("Doctor").specialization("dentist").build();

        when(doctorRepository.findById(command.doctorId())).thenReturn(Optional.of(doctor));

        //when
        InvalidAppointmentTimeException exception = assertThrows(InvalidAppointmentTimeException.class,
                () -> appointmentService.add(command));
        //then
        assertAll(
                () -> assertEquals("Appointment times must be aligned to 15-minute intervals", exception.getMessage()),
                () -> assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus())
        );
    }

    @Test
    void add_OverlappingAppointment_AppointmentOverlapExceptionThrown() {
        //given
        AppointmentCreateCommand command = AppointmentCreateCommand.builder()
                .doctorId(1L)
                .startTime(LocalDateTime.now().plusDays(2).withHour(10).withMinute(15).withSecond(0).withNano(0))
                .endTime(LocalDateTime.now().plusDays(2).withHour(10).withMinute(30).withSecond(0).withNano(0))
                .build();
        Doctor doctor = Doctor.builder().id(1L).build();
        List<Appointment> existingAppointments = List.of(
                Appointment.builder()
                        .id(1L)
                        .doctor(doctor)
                        .startTime(LocalDateTime.now().plusDays(2).withHour(9).withMinute(30).withSecond(0).withNano(0))
                        .endTime(LocalDateTime.now().plusDays(2).withHour(10).withMinute(0).withSecond(0).withNano(0))
                        .build(),
                Appointment.builder().
                        id(2L)
                        .doctor(doctor)
                        .startTime(LocalDateTime.now().plusDays(2).withHour(10).withMinute(30).withSecond(0).withNano(0))
                        .endTime(LocalDateTime.now().plusDays(2).withHour(11).withMinute(0).withSecond(0).withNano(0))
                        .build()
        );

        when(doctorRepository.findById(command.doctorId())).thenReturn(Optional.of(doctor));
        when(appointmentRepository.existsByDoctorIdAndTimeRange(doctor.getId(), command.startTime(), command.endTime()))
                .thenReturn(List.of(existingAppointments.get(1)));

        //when
        AppointmentOverlapException exception = assertThrows(AppointmentOverlapException.class,
                () -> appointmentService.add(command));
        //then
        assertAll(
                () -> assertFalse(exception.getConflicts().isEmpty()),
                () -> assertEquals(2L, exception.getConflicts().get(0).getId())
        );
    }

    @Test
    void book_DataCorrect_AppointmentReturned() {
        //given
        BookAppointmentCommand command = new BookAppointmentCommand(1L, 2L);
        Doctor doctor = Doctor.builder().id(2L).firstName("John").lastName("Doctor").specialization("dentist").build();
        Appointment foundAppointment = Appointment.builder()
                .id(1L)
                .doctor(doctor)
                .patient(null)
                .startTime(LocalDateTime.now().plusDays(2).withHour(10).withMinute(45).withSecond(0).withNano(0))
                .endTime(LocalDateTime.now().plusDays(2).withHour(11).withMinute(45).withSecond(0).withNano(0))
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
                () -> assertEquals(LocalDateTime.now().plusDays(2).withHour(10).withMinute(45).withSecond(0).withNano(0), result.getStartTime()),
                () -> assertEquals(LocalDateTime.now().plusDays(2).withHour(11).withMinute(45).withSecond(0).withNano(0), result.getEndTime())
        );
    }

    @Test
    void book_AppointmentNotFound_AppointmentNotFoundExceptionThrown() {
        //given
        BookAppointmentCommand command = BookAppointmentCommand.builder()
                .appointmentId(1L)
                .build();

        when(appointmentRepository.findById(command.appointmentId())).thenReturn(Optional.empty());

        //when
        AppointmentNotFoundException exception = assertThrows(AppointmentNotFoundException.class,
                () -> appointmentService.book(command));

        //then
        assertAll(
                () -> assertEquals("Appointment with id 1 not found", exception.getMessage()),
                () -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus())
        );
    }

    @Test
    void book_AppointmentAlreadyTaken_AppointmentAlreadyBookedExceptionThrown() {
        //given
        BookAppointmentCommand command = BookAppointmentCommand.builder()
                .appointmentId(1L)
                .build();
        Appointment appointment = Appointment.builder()
                .id(1L)
                .patient(Patient.builder().id(2L).build())
                .build();

        when(appointmentRepository.findById(command.appointmentId())).thenReturn(Optional.of(appointment));

        //when
        AppointmentAlreadyBookedException exception = assertThrows(AppointmentAlreadyBookedException.class,
                () -> appointmentService.book(command));

        //then
        assertAll(
                () -> assertEquals("The appointment is already taken", exception.getMessage()),
                () -> assertEquals(HttpStatus.CONFLICT, exception.getStatus())
        );
    }

    @Test
    void book_StartTimeInPast_InvalidAppointmentTimeExceptionThrown() {
        //given
        BookAppointmentCommand command = BookAppointmentCommand.builder()
                .appointmentId(1L)
                .build();
        Appointment appointment = Appointment.builder()
                .id(1L)
                .startTime(LocalDateTime.now().minusHours(2))
                .build();

        when(appointmentRepository.findById(command.appointmentId())).thenReturn(Optional.of(appointment));

        //when
        InvalidAppointmentTimeException exception = assertThrows(InvalidAppointmentTimeException.class,
                () -> appointmentService.book(command));

        //then
        assertAll(
                () -> assertEquals("Appointment start time must be in the future", exception.getMessage()),
                () -> assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus())
        );
    }

    @Test
    void book_PatientNotFound_PatientNotFoundExceptionThrown() {
        //given
        BookAppointmentCommand command = BookAppointmentCommand.builder()
                .appointmentId(1L)
                .patientId(3L)
                .build();
        Appointment appointment = Appointment.builder()
                .id(1L)
                .startTime(LocalDateTime.now().plusDays(2).withHour(10).withMinute(45).withSecond(0).withNano(0))
                .endTime(LocalDateTime.now().plusDays(2).withHour(11).withMinute(45).withSecond(0).withNano(0))
                .build();

        when(appointmentRepository.findById(command.appointmentId())).thenReturn(Optional.of(appointment));
        when(patientRepository.findById(command.patientId())).thenReturn(Optional.empty());

        //when
        PatientNotFoundException exception = assertThrows(PatientNotFoundException.class,
                () -> appointmentService.book(command));

        //then
        assertAll(
                () -> assertEquals("Patient with id 3 not found", exception.getMessage()),
                () -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus())
        );
    }
}