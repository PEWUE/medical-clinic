package com.PEWUE.medical_clinic.validator;

import com.PEWUE.medical_clinic.exception.UsernameAlreadyExistsException;
import com.PEWUE.medical_clinic.model.User;
import com.PEWUE.medical_clinic.repository.UserRepository;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserValidator {
    public static void validateCreateUser(User user, UserRepository userRepository) {
        if (user.getUsername() == null || user.getPassword() == null) {
            throw new IllegalArgumentException("Fields should not be null");
        }
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new UsernameAlreadyExistsException(user.getUsername());
        }
    }

    public static void validateEditUser(User user, UserRepository userRepository) {
        if (user.getUsername() == null || user.getPassword() == null) {
            throw new IllegalArgumentException("Fields should not be null");
        }
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new UsernameAlreadyExistsException(user.getUsername());
        }
    }

    public static void validatePassword(String password) {
        if (password == null) {
            throw new IllegalArgumentException("Field should not be null");
        }
    }
}
