package com.PEWUE.medical_clinic.validator;

import com.PEWUE.medical_clinic.exception.EmailAlreadyExistsException;
import com.PEWUE.medical_clinic.exception.UsernameAlreadyExistsException;
import com.PEWUE.medical_clinic.model.User;
import com.PEWUE.medical_clinic.repository.UserRepository;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserValidator {
    public static void validateCreateUser(User user, UserRepository userRepository) {
        //TODO ogarnąć ify
        if (user.getId() == null &&
                (user.getUsername() == null ||
                        user.getPassword() == null ||
                        user.getEmail() == null)) {
            throw new IllegalArgumentException("Fields should not be null");
        }
        if (user.getId() == null && userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException(user.getEmail());
        }
        if (user.getId() == null && userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new UsernameAlreadyExistsException(user.getUsername());
        }
        if (user.getId() != null && userRepository.findById(user.getId()).isEmpty()) {
            throw new IllegalArgumentException("User with given id does not exist");
        }
    }

    public static void validateEditUser(User user, UserRepository userRepository) {
        if (user.getUsername() == null || user.getPassword() == null || user.getEmail() == null) {
            throw new IllegalArgumentException("Fields should not be null");
        }
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException(user.getEmail());
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
