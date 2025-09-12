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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DoctorService {
    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;

    public Page<Doctor> find(Pageable pageable) {
        return doctorRepository.findAll(pageable);
    }

    public Doctor find(String email) {
        return doctorRepository.findByUserEmail(email)
                .orElseThrow(() -> new DoctorNotFoundException(email));
    }

    @Transactional
    public Doctor add(Doctor doctor) {
        DoctorValidator.validateCreateDoctor(doctor);
        assignUserToDoctor(doctor);
        UserValidator.validateCreateUser(doctor.getUser(),userRepository);
        return doctorRepository.save(doctor);
    }

    @Transactional
    public void delete(String email) {
        Doctor doctor = find(email);
        doctorRepository.delete(doctor);
    }

    @Transactional
    public Doctor edit(String email, Doctor updatedDoctor) {
        Doctor doctor = find(email);
        DoctorValidator.validateEditDoctor(updatedDoctor);
        doctor.edit(updatedDoctor);
        return doctorRepository.save(doctor);
    }

    private void assignUserToDoctor(Doctor doctor) {
        if (doctor.getUser() != null && doctor.getUser().getId() != null) {
            User existingUser = userRepository.findById(doctor.getUser().getId())
                    .orElseThrow(() -> new UserNotFoundException(doctor.getUser().getId()));
            doctor.setUser(existingUser);
        }
    }
}
