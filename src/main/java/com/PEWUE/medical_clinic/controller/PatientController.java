package com.PEWUE.medical_clinic.controller;

import com.PEWUE.medical_clinic.command.ChangePasswordCommand;
import com.PEWUE.medical_clinic.dto.PatientDto;
import com.PEWUE.medical_clinic.mapper.PatientMapper;
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
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/patients")
public class PatientController {
    private final PatientService patientService;
    private final PatientMapper patientMapper;

    @GetMapping
    public List<PatientDto> getPatients() {
        return patientService.getAllPatients().stream()
                .map(patientMapper::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{email}")
    public PatientDto getPatientByEmail(@PathVariable String email) {
        return patientMapper.toDto(patientService.getPatientByEmail(email));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PatientDto addPatient(@RequestBody Patient patient) {
        return patientMapper.toDto(patientService.addPatient(patient));
    }

    @DeleteMapping("/{email}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removePatient(@PathVariable String email) {
        patientService.removePatient(email);
    }

    @PutMapping("/{email}")
    public PatientDto editPatient(@PathVariable String email, @RequestBody Patient patient) {
        return patientMapper.toDto(patientService.editPatient(email, patient));
    }

    @PatchMapping("/{email}")
    public PatientDto changePassword(@PathVariable String email, @RequestBody ChangePasswordCommand command) {
        return patientMapper.toDto(patientService.changePassword(email, command.getPassword()));
    }
}
