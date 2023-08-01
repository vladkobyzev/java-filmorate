package ru.yandex.practicum.filmorate.controllers;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.service.UserServiceImpl;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UsersController {
    private final UserServiceImpl userServiceImpl;

    @Autowired
    public UsersController(UserServiceImpl userServiceImpl) {
        this.userServiceImpl = userServiceImpl;
    }


    @GetMapping("/{id}")
    public User getUser(@PathVariable(required = false) int id) {
        return userServiceImpl.getUser(id);
    }

    @GetMapping()
    public List<User> getAllUsers() {
        return userServiceImpl.getAllUsers();
    }

    @GetMapping("/{id}/friends")
    public List<User> getUsersFriends(@PathVariable int id) {
        return userServiceImpl.getUsersFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable(required = false) int id, @PathVariable(required = false) int otherId) {
        return userServiceImpl.getCommonFriends(id, otherId);
    }

    @GetMapping("/{id}/recommendations")
    public List<Film> getRecommendations(@PathVariable int id) {
        return userServiceImpl.getRecommendations(id);
    }

    @PostMapping()
    public User createUser(@Valid @RequestBody User user) {
        return userServiceImpl.create(buildUser(user));
    }

    @PutMapping()
    public User updateUser(@Valid @RequestBody User user) {
        return userServiceImpl.update(buildUser(user));
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable int id, @PathVariable int friendId) {
        userServiceImpl.addFriend(id, friendId);
    }


    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable int id, @PathVariable int friendId) {
        userServiceImpl.deleteFriend(id, friendId);
    }

    @DeleteMapping("/{id}")
    public void deleteUserById(@PathVariable int id) {
        userServiceImpl.deleteUserById(id);
    }



    private User buildUser(User user) {
        return User.builder()
                .id(user.getId())
                .email(user.getEmail())
                .login(user.getLogin())
                .name(user.getName())
                .birthday(user.getBirthday())
                .build();
    }
}
