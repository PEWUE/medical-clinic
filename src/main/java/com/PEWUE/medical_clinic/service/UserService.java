package com.PEWUE.medical_clinic.service;

import com.PEWUE.medical_clinic.exception.UserNotFoundException;
import com.PEWUE.medical_clinic.model.User;
import com.PEWUE.medical_clinic.repository.UserRepository;
import com.PEWUE.medical_clinic.validator.UserValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User addUser(User user) {
        UserValidator.validateCreateUser(user, userRepository);
        return userRepository.save(user);
    }

    public void removeUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        userRepository.delete(user);
    }
}
