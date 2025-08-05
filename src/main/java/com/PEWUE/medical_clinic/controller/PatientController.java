package com.PEWUE.medical_clinic.controller;

import com.PEWUE.medical_clinic.command.ChangePasswordCommand;
import com.PEWUE.medical_clinic.model.Patient;
import com.PEWUE.medical_clinic.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/patients")
public class PatientController {
    private final PatientService patientService;

    @GetMapping
    public List<Patient> getPatients() {
        return patientService.getAllPatients();
    }

    @GetMapping("/{email}")
    public Patient getPatientByEmail(@PathVariable String email) {
        return patientService.getPatientByEmail(email);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Patient addPatient(@RequestBody Patient patient) {
        return patientService.addPatient(patient);
    }

    @DeleteMapping("/{email}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removePatient(@PathVariable String email) {
        patientService.removePatient(email);
    }

    @PutMapping("/{email}")
    public Patient editPatient(@PathVariable String email, @RequestBody Patient patient) {
        return patientService.editPatient(email, patient);
    }

    @PatchMapping("/{email}")
    public Patient changePassword(@PathVariable String email, @RequestBody ChangePasswordCommand command) {
        return patientService.changePassword(email, command.getPassword());
    }
}
