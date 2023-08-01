package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.user.User;

import java.util.List;

public interface UserRepository {
    User getUser(int id);

    List<User> getAllUsers();

    List<User> getUsersFriends(int id);

    List<User> getCommonFriends(int id, int otherId);

    List<Film> getRecommendations(int id);

    User createUser(User user);

    User update(User user, int id);

    void addFriend(int id, int friendId);

    void deleteFriend(int id, int friendId);

    void deleteUserById(int id);
}
