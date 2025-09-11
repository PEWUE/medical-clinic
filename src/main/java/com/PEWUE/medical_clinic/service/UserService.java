package com.PEWUE.medical_clinic.service;

import com.PEWUE.medical_clinic.exception.UserNotFoundException;
import com.PEWUE.medical_clinic.model.User;
import com.PEWUE.medical_clinic.repository.UserRepository;
import com.PEWUE.medical_clinic.validator.UserValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Transactional
    public User add(User user) {
        UserValidator.validateCreateUser(user, userRepository);
        return userRepository.save(user);
    }

    @Transactional
    public User changePassword(Long id, String password) {
        UserValidator.validatePassword(password);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        user.setPassword(password);
        return userRepository.save(user);
    }
}
