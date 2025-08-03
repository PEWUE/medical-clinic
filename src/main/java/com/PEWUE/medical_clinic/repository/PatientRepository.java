package com.PEWUE.medical_clinic.repository;

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

    public boolean add(Patient patient) {
        if (findByEmail(patient.getEmail()).isPresent()) {
            return false;
        }
        patients.add(patient);
        return true;
    }

    public boolean remove(String email) {
        return patients.removeIf(patient -> patient.getEmail().equals(email));
    }

    public boolean edit(String email, Patient updatedPatient) {
        for (int i = 0; i < patients.size(); i++) {
            Patient current = patients.get(i);
            if (current.getEmail().equals(email)) {
                if (!email.equals(updatedPatient.getEmail()) && findByEmail(updatedPatient.getEmail()).isPresent()) {
                    return false;
                }
                patients.set(i, updatedPatient);
                return true;
            }
        }
        return false;
    }
}
