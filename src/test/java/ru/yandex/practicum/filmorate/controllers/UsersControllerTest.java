package ru.yandex.practicum.filmorate.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.service.UserServiceImpl;
import ru.yandex.practicum.filmorate.exception_handler.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.user.User;

import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureMockMvc
class UsersControllerTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    UserServiceImpl userServiceImpl;
    @Test
    void shouldReturnAllUsers() {

        List<User> allUsers = List.of(User.builder()
                .name("kesha@gmail.com")
                .email("qqqq")
                .login("kesha")
                .birthday(LocalDate.of(2002,2,23))
                .build());
        when(userServiceImpl.getAllUsers()).thenReturn(allUsers);
        UsersController userController = new UsersController(userServiceImpl);
        assertEquals(List.of(User.builder()
                .name("kesha@gmail.com")
                .email("qqqq")
                .login("kesha")
                .birthday(LocalDate.of(2002,2,23))
                .build()), userController.getAllUsers());
    }

    @Test
    void shouldReturnAllUsersFriends() throws Exception {
        List<User> allUsers = List.of(User.builder()
                .id(2)
                .name("kesha@gmail.com")
                .email("qqqq")
                .login("kesha")
                .birthday(LocalDate.of(2002,2,23))
                .build());
        when(userServiceImpl.getUsersFriends(1)).thenReturn(allUsers);

        mockMvc.perform(
                        get("/users/1/friends")
                                .content(objectMapper.writeValueAsString(allUsers))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnAllCommonFriends() throws Exception {
        List<User> allUsers = List.of(User.builder()
                .id(3)
                .name("kesha@gmail.com")
                .email("qqqq")
                .login("kesha")
                .birthday(LocalDate.of(2002,2,23))
                .build());
        when(userServiceImpl.getCommonFriends(1,2)).thenReturn(allUsers);

        mockMvc.perform(
                        get("/users/1/friends/common/2")
                                .content(objectMapper.writeValueAsString(allUsers))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());
    }

    @Test
    void shouldCreateUser() throws Exception {
        User user = User.builder()
                .name("KEsha")
                .email("kesha@gmail.com")
                .login("kesha")
                .birthday(LocalDate.of(2002,2,23))
                .build();
        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(user))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());
    }
    @Test
    void shouldUpdateUser() throws Exception {
        User user = User.builder()
                .id(1)
                .name("KEsha")
                .email("kesha@gmail.com")
                .login("kesha")
                .birthday(LocalDate.of(2002,2,23))
                .build();
        when(userServiceImpl.getUser(1)).thenReturn(user);
        mockMvc.perform(
                        get("/users/1")
                                .content(objectMapper.writeValueAsString(user))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        user.setName("updatedName");
        when(userServiceImpl.update(user)).thenReturn(user);
        mockMvc.perform(
                        put("/users")
                                .content(objectMapper.writeValueAsString(user))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());


    }

    @Test
    void whenNotValidUser_shouldReturnValidationException() throws Exception {
        User user = User.builder()
                .name("keshagmailcom")
                .email("qqqq")
                .login("kesha")
                .birthday(LocalDate.of(2002,2,23))
                .build();
        when(userServiceImpl.create(user)).thenThrow(new ValidationException("Error"));
        mockMvc.perform(
                        post("/users").header("Content-Type", "application/json").content(objectMapper.writeValueAsString(user))
                ).andExpect(status().isBadRequest())
                .andExpect((mvcResult -> mvcResult.getResolvedException().getClass().equals(ValidationException.class)));

    }

    @Test
    void shouldCreateOk_afterTheUpdateUser() throws Exception {
        User user = User.builder()
                .name("qqqq")
                .email("kesha@gmail.com")
                .login("kesha")
                .birthday(LocalDate.of(2002,2,23))
                .build();
        when(userServiceImpl.update(user)).thenReturn(user);
        mockMvc.perform(
                        put("/users")
                                .content(objectMapper.writeValueAsString(user))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());
    }
    @Test
    void methodUpdateShouldReturnException() throws Exception {
        User user = User.builder()
                .name("keshagmailcom")
                .email("qqqq")
                .login("kesha")
                .birthday(LocalDate.of(2002,2,23))
                .build();
        when(userServiceImpl.update(user)).thenThrow(ValidationException.class);
        mockMvc.perform(
                        put("/users")
                                .content(objectMapper.writeValueAsString(user))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect((mvcResult -> mvcResult.getResolvedException().getClass().equals(ValidationException.class)));
    }

    @Test
    void methodShouldOneUser() throws Exception {
        User user = User.builder()
                .id(1)
                .name("keshagmailcom")
                .email("qqqq")
                .login("kesha")
                .birthday(LocalDate.of(2002,2,23))
                .build();
        when(userServiceImpl.getUser(1)).thenReturn(user);

        mockMvc.perform(
                        get("/users/1")
                                .content(objectMapper.writeValueAsString(user))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());
    }
}