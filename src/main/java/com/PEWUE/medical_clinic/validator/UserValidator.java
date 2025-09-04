package com.PEWUE.medical_clinic.validator;

import com.PEWUE.medical_clinic.exception.EmailAlreadyExistsException;
import com.PEWUE.medical_clinic.exception.FieldsShouldNotBeNullException;
import com.PEWUE.medical_clinic.exception.UserNotFoundException;
import com.PEWUE.medical_clinic.exception.UsernameAlreadyExistsException;
import com.PEWUE.medical_clinic.model.User;
import com.PEWUE.medical_clinic.repository.UserRepository;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserValidator {

    public static void validateCreateUser(User user, UserRepository userRepository) {
        if (user.getId() == null) {
            if (user.getUsername() == null || user.getPassword() == null || user.getEmail() == null) {
                throw new FieldsShouldNotBeNullException();
            }
            if (userRepository.findByEmail(user.getEmail()).isPresent()) {
                throw new EmailAlreadyExistsException(user.getEmail());
            }
            if (userRepository.findByUsername(user.getUsername()).isPresent()) {
                throw new UsernameAlreadyExistsException(user.getUsername());
            }
        } else {
            if (userRepository.findById(user.getId()).isEmpty()) {
                throw new UserNotFoundException(user.getId());
            }
        }
    }

    public static void validateEditUser(User user, UserRepository userRepository) {
        if (user.getUsername() == null || user.getPassword() == null || user.getEmail() == null) {
            throw new FieldsShouldNotBeNullException();
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
            throw new FieldsShouldNotBeNullException();
        }
    }
}
