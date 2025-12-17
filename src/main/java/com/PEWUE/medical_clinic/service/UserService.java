package com.PEWUE.medical_clinic.service;

import com.PEWUE.medical_clinic.exception.UserNotFoundException;
import com.PEWUE.medical_clinic.model.User;
import com.PEWUE.medical_clinic.repository.UserRepository;
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
public class UserService {
    private final UserRepository userRepository;

    public Page<User> find(Pageable pageable) {
        log.info("Finding users with pageable={}", pageable);
        return userRepository.findAll(pageable);
    }

    @Transactional
    public User add(User user) {
        log.info("Adding new user {}", user.getUsername());
        UserValidator.validateCreateUser(user, userRepository);
        log.info("Successfully added new user {}", user.getUsername());
        return userRepository.save(user);
    }

    @Transactional
    public User changePassword(Long id, String password) {
        log.info("Changing password for user with id {}", id);
        UserValidator.validatePassword(password);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        user.setPassword(password);
        log.info("Password changed successfully for user with id {}", id);
        return userRepository.save(user);
    }
}
