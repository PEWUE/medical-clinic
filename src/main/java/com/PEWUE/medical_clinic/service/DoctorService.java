package com.PEWUE.medical_clinic.service;

import com.PEWUE.medical_clinic.exception.PatientNotFoundException;
import com.PEWUE.medical_clinic.exception.UserNotFoundException;
import com.PEWUE.medical_clinic.model.Doctor;
import com.PEWUE.medical_clinic.model.User;
import com.PEWUE.medical_clinic.repository.DoctorRepository;
import com.PEWUE.medical_clinic.repository.UserRepository;
import com.PEWUE.medical_clinic.validator.DoctorValidator;
import com.PEWUE.medical_clinic.validator.UserValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorService {
    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;

    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    public Doctor addDoctor(Doctor doctor) {
        DoctorValidator.validateCreateDoctor(doctor, doctorRepository);
        if (doctor.getUser() != null && doctor.getUser().getId() != null) {
            User existingUser = userRepository.findById(doctor.getUser().getId())
                    .orElseThrow(() -> new UserNotFoundException(doctor.getUser().getId()));
            doctor.setUser(existingUser);
        }
        UserValidator.validateCreateUser(doctor.getUser(),userRepository);
        return doctorRepository.save(doctor);
    }

    public void removeDoctor(String email) {
        Doctor doctor = doctorRepository.findByEmail(email).orElseThrow(() -> new PatientNotFoundException(email));
        doctorRepository.delete(doctor);
    }

    public Doctor editDoctor(String email, Doctor updatedDoctor) {
        Doctor doctor = doctorRepository.findByEmail(email)
                .orElseThrow(() -> new PatientNotFoundException(email));
        DoctorValidator.validateEditDoctor(doctor, updatedDoctor, doctorRepository);
        UserValidator.validateEditUser(updatedDoctor.getUser(), userRepository);
        doctor.edit(updatedDoctor);
        return doctorRepository.save(doctor);
    }
}
