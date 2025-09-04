package com.PEWUE.medical_clinic.service;

import com.PEWUE.medical_clinic.exception.DoctorNotFoundException;
import com.PEWUE.medical_clinic.exception.IntitutionNotFoundException;
import com.PEWUE.medical_clinic.exception.UserNotFoundException;
import com.PEWUE.medical_clinic.model.Doctor;
import com.PEWUE.medical_clinic.model.Institution;
import com.PEWUE.medical_clinic.model.User;
import com.PEWUE.medical_clinic.repository.DoctorRepository;
import com.PEWUE.medical_clinic.repository.InstitutionRepository;
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
    private final InstitutionRepository institutionRepository;

    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    public Doctor getDoctorByEmail(String email) {
        return doctorRepository.findByUserEmail(email)
                .orElseThrow(() -> new DoctorNotFoundException(email));
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
        Doctor doctor = getDoctorByEmail(email);
        doctorRepository.delete(doctor);
    }

    public Doctor editDoctor(String email, Doctor updatedDoctor) {
        Doctor doctor = getDoctorByEmail(email);
        DoctorValidator.validateEditDoctor(doctor, updatedDoctor, doctorRepository);
        doctor.edit(updatedDoctor);
        return doctorRepository.save(doctor);
    }
}
