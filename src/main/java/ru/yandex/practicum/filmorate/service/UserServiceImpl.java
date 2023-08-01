package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.exception_handler.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.repository.UserRepository;
import ru.yandex.practicum.filmorate.exception_handler.exceptions.RequiredObjectWasNotFound;
import java.util.*;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(@Qualifier("userRepositoryImpl") UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public User getUser(int id) {
        try {
            return userRepository.getUser(id);
        } catch (EmptyResultDataAccessException e) {
            throw new RequiredObjectWasNotFound("User " + id + " not found");
        }
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.getAllUsers();
    }

    @Override
    public List<User> getUsersFriends(int id) {
        return userRepository.getUsersFriends(id);
    }

    public List<Film> getRecommendations(int id) {
        return userRepository.getRecommendations(id);
    }

    @Override
    public List<User> getCommonFriends(int id, int otherId) {
        List<User> commonFriends = userRepository.getCommonFriends(id, otherId);
        return commonFriends.isEmpty() ? Collections.EMPTY_LIST : commonFriends;
    }


    @Override
    public User create(User user) {
        return userRepository.createUser(user);
    }

    @Override
    public User update(User user) {
        try {
            return userRepository.update(user, user.getId());
        } catch (EmptyResultDataAccessException e) {
            throw new RequiredObjectWasNotFound("User " + user.getId() + " not found");
        }
    }


    @Override
    public void addFriend(int id, int friendId) {
        try {
            userRepository.addFriend(id, friendId);
        } catch (EmptyResultDataAccessException e) {
            throw new ValidationException("User not found");
        }
        log.info("Users " + id + " and " + friendId + "have become friends");
    }

    @Override
    public void deleteFriend(int id, int friendId) {
        userRepository.deleteFriend(id, friendId);
        log.info("User " + id + " deleted user " + friendId + "from friends");
    }

    @Override
    public void deleteUserById(int id) {
        userRepository.deleteUserById(id);
        log.info("Пользователь с id {} удален", id);
    }
}
