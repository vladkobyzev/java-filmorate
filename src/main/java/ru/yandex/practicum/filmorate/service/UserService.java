package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.user.User;

import java.util.List;

public interface UserService {

    User getUser(int id);

    List<User> getAllUsers();

    List<User> getUsersFriends(int id);

    List<User> getCommonFriends(int id, int otherId);

    User create(User user);

    User update(User user);

    void addFriend(int id, int friendId);

    void deleteFriend(int id, int friendId);

    void deleteUserById(int id);
}
