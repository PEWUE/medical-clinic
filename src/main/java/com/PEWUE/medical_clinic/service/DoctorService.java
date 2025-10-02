package com.PEWUE.medical_clinic.service;

import com.PEWUE.medical_clinic.exception.DoctorNotFoundException;
import com.PEWUE.medical_clinic.exception.UserNotFoundException;
import com.PEWUE.medical_clinic.model.Doctor;
import com.PEWUE.medical_clinic.model.User;
import com.PEWUE.medical_clinic.repository.DoctorRepository;
import com.PEWUE.medical_clinic.repository.UserRepository;
import com.PEWUE.medical_clinic.validator.DoctorValidator;
import com.PEWUE.medical_clinic.validator.UserValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DoctorService {
    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;

    public Page<Doctor> find(Pageable pageable) {
        log.info("Finding doctors with pageable={}", pageable);
        return doctorRepository.findAll(pageable);
    }

    public Doctor find(String email) {
        log.info("Finding doctors with email {}", email);
        return doctorRepository.findByUserEmail(email)
                .orElseThrow(() -> new DoctorNotFoundException(email));
    }

    @Transactional
    public Doctor add(Doctor doctor) {
        log.info("Adding new doctor {} {}", doctor.getFirstName(), doctor.getLastName());
        DoctorValidator.validateCreateDoctor(doctor);
        assignUserToDoctor(doctor);
        UserValidator.validateCreateUser(doctor.getUser(), userRepository);
        log.info("Successfully added doctor with email {}", doctor.getUser().getEmail());
        return doctorRepository.save(doctor);
    }

    @Transactional
    public void delete(String email) {
        log.info("Deleting doctor with email {}", email);
        Doctor doctor = find(email);
        doctorRepository.delete(doctor);
        log.info("Doctor with email {} was deleted", email);
    }

    @Transactional
    public Doctor edit(String email, Doctor updatedDoctor) {
        log.info("Editing doctor with email {}", email);
        Doctor doctor = find(email);
        DoctorValidator.validateEditDoctor(updatedDoctor);
        doctor.edit(updatedDoctor);
        log.info("Successfully edited doctor with email {}", email);
        return doctorRepository.save(doctor);
    }

    private void assignUserToDoctor(Doctor doctor) {
        if (doctor.getUser() != null && doctor.getUser().getId() != null) {
            log.info("Assigning existing user with id {} to doctor", doctor.getUser().getId());
            User existingUser = userRepository.findById(doctor.getUser().getId())
                    .orElseThrow(() -> new UserNotFoundException(doctor.getUser().getId()));
            doctor.setUser(existingUser);
        }
    }
}
