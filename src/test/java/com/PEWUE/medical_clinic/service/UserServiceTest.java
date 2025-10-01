package com.PEWUE.medical_clinic.service;

import com.PEWUE.medical_clinic.exception.EmailAlreadyExistsException;
import com.PEWUE.medical_clinic.exception.FieldsShouldNotBeNullException;
import com.PEWUE.medical_clinic.exception.UserNotFoundException;
import com.PEWUE.medical_clinic.exception.UsernameAlreadyExistsException;
import com.PEWUE.medical_clinic.model.User;
import com.PEWUE.medical_clinic.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {
    UserRepository userRepository;
    UserService userService;

    @BeforeEach
    void setup() {
        this.userRepository = mock(UserRepository.class);
        this.userService = new UserService(userRepository);
    }

    @Test
    void find_DataCorrect_UsersReturned() {
        //given
        Pageable pageable = PageRequest.of(0,2);
        List<User> users = List.of(
                User.builder().id(1L).email("email1@user.com").username("username1").password("password1").build(),
                User.builder().id(2L).email("email2@user.com").username("username2").password("password2").build()
        );
        Page<User> page = new PageImpl<>(users, pageable, users.size());

        when(userRepository.findAll(pageable)).thenReturn(page);

        //when
        Page<User> result = userService.find(pageable);

        //then
        assertAll(
                () -> assertEquals(2, result.getContent().size()),
                () -> assertEquals(1L, result.getContent().get(0).getId()),
                () -> assertEquals("email1@user.com", result.getContent().get(0).getEmail()),
                () -> assertEquals("username1", result.getContent().get(0).getUsername()),
                () -> assertEquals("password1", result.getContent().get(0).getPassword()),
                () -> assertEquals(2L, result.getContent().get(1).getId()),
                () -> assertEquals("email2@user.com", result.getContent().get(1).getEmail()),
                () -> assertEquals("username2", result.getContent().get(1).getUsername()),
                () -> assertEquals("password2", result.getContent().get(1).getPassword())
        );
        verify(userRepository).findAll(pageable);
    }

    @Test
    void add_DataCorrect_UserReturned() {
        //given
        User inputUser = User.builder()
                .email("email1@user.com")
                .username("username1")
                .password("password1")
                .build();
        User user = User.builder()
                .id(1L)
                .email("email1@user.com")
                .username("username1")
                .password("password1")
                .build();

        when(userRepository.save(inputUser)).thenReturn(user);

        //when
        User result = userService.add(inputUser);

        //then
        assertAll(
                () -> assertEquals(1L, result.getId()),
                () -> assertEquals("email1@user.com", result.getEmail()),
                () -> assertEquals("username1", result.getUsername()),
                () -> assertEquals("password1", result.getPassword())
        );
        verify(userRepository).save(inputUser);
    }

    @Test
    void add_UserUsernameFieldIsNull_FieldsShouldNotBeNullExceptionThrown() {
        //given
        User user = User.builder()
                .email("user@email.com")
                .password("pa$$word")
                .build();

        //when
        FieldsShouldNotBeNullException exception = assertThrows(FieldsShouldNotBeNullException.class,
                () -> userService.add(user));

        //then
        assertAll(
                () -> assertEquals("Fields should not be null", exception.getMessage()),
                () -> assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus())
        );
    }

    @Test
    void add_EmailAlreadyExists_EmailAlreadyExistsExceptionThrown() {
        //given
        User user = User.builder()
                .email("user@email.com")
                .username("username1")
                .password("pa$$word")
                .build();

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        //when
        EmailAlreadyExistsException exception = assertThrows(EmailAlreadyExistsException.class,
                () -> userService.add(user));

        //then
        assertAll(
                () -> assertEquals("Email user@email.com is already taken", exception.getMessage()),
                () -> assertEquals(HttpStatus.CONFLICT, exception.getStatus())
        );
    }

    @Test
    void add_UsernameAlreadyExists_UsernameAlreadyExistsExceptionThrown() {
        //given
        User user = User.builder()
                .email("user@email.com")
                .username("username1")
                .password("pa$$word")
                .build();

        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

        //when
        UsernameAlreadyExistsException exception = assertThrows(UsernameAlreadyExistsException.class,
                () -> userService.add(user));

        //then
        assertAll(
                () -> assertEquals("Username username1 is already taken", exception.getMessage()),
                () -> assertEquals(HttpStatus.CONFLICT, exception.getStatus())
        );
    }

    @Test
    void changePassword_DataCorrect_UserReturned() {
        //given
        Long userId = 3L;
        String newPassword = "MyNewPa$$word123!";
        User user = User.builder()
                .id(3L)
                .email("email3@user.com")
                .username("username3")
                .password("oldPassword")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        //when
        User result = userService.changePassword(userId, newPassword);

        //then
        assertAll(
                () -> assertEquals(3L, result.getId()),
                () -> assertEquals("email3@user.com", result.getEmail()),
                () -> assertEquals("username3", result.getUsername()),
                () -> assertEquals("MyNewPa$$word123!", result.getPassword())
        );
        verify(userRepository).findById(userId);
        verify(userRepository).save(user);
    }

    @Test
    void changePassword_PasswordFieldIsNull_FieldsShouldNotBeNullExceptionThrown() {
        //given
        Long userId = 5L;
        String password = null;

        //when
        FieldsShouldNotBeNullException exception = assertThrows(FieldsShouldNotBeNullException.class,
                () -> userService.changePassword(userId, password));

        //then
        assertAll(
                () -> assertEquals("Fields should not be null", exception.getMessage()),
                () -> assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus())
        );
    }

    @Test
    void changePassword_UserNotFound_UserNotFoundExceptionThrown() {
        //given
        Long userId = 5L;
        String password = "pa$$word";

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        //when
        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> userService.changePassword(userId, password));

        //then
        assertAll(
                () -> assertEquals("User with id 5 not found", exception.getMessage()),
                () -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus())
        );
    }
}