package com.PEWUE.medical_clinic.controller;

import com.PEWUE.medical_clinic.model.User;
import com.PEWUE.medical_clinic.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
    @MockitoBean
    UserService userService;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    MockMvc mockMvc;

    @Test
    void shouldReturnPagedUserDtosWhenDataCorrect() throws Exception {
        List<User> users = List.of(
                User.builder().id(1L).email("email1@user.com").username("user1").password("password1").build(),
                User.builder().id(2L).email("email2@user.com").username("user2").password("password2").build()
        );
        Pageable pageable = PageRequest.of(0,2);
        Page<User> page = new PageImpl<>(users, pageable, users.size());

        when(userService.find(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/users")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isOk(),
                jsonPath("$.content[0].id").value(1L),
                jsonPath("$.content[0].email").value("email1@user.com"),
                jsonPath("$.content[0].username").value("user1")
        );
    }
}
