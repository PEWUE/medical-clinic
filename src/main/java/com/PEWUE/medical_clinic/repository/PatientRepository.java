package com.PEWUE.medical_clinic.repository;

import com.PEWUE.medical_clinic.exceptions.EmailAlreadyExistsException;
import com.PEWUE.medical_clinic.model.Patient;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class PatientRepository {
    private final List<Patient> patients = new ArrayList<>();

    public List<Patient> getAll() {
        return new ArrayList<>(patients);
    }

    public Optional<Patient> findByEmail(String email) {
        return patients.stream()
                .filter(patient -> patient.getEmail().equals(email))
                .findFirst();
    }

    public Patient add(Patient patient) {
        if (findByEmail(patient.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Email " + patient.getEmail() + " is already taken");
        }
        patients.add(patient);
        return patient;
    }

    public boolean remove(String email) {
        return patients.removeIf(patient -> patient.getEmail().equals(email));
    }

    public Patient edit(Patient patient, Patient updatedPatient) {
        patient.setFirstName(updatedPatient.getFirstName());
        patient.setLastName(updatedPatient.getLastName());
        patient.setEmail(updatedPatient.getEmail());
        patient.setPassword(updatedPatient.getPassword());
        patient.setIdCardNo(updatedPatient.getIdCardNo());
        patient.setPhoneNumber(updatedPatient.getPhoneNumber());
        patient.setBirthday(updatedPatient.getBirthday());
        return patient;
    }
}
