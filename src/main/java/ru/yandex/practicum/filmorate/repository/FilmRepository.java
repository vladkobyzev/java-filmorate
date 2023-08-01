package ru.yandex.practicum.filmorate.repository;


import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.Genre;
import ru.yandex.practicum.filmorate.model.film.Mpa;

import java.util.List;

public interface FilmRepository {
    int createFilm(Film film);

    Film update(Film film, int id);

    List<Film> getAllFilms();

    List<Film> getPopularFilms(int count);

    Film getFilm(int id);

    void like(int filmId, int userId);

    int deleteLike(int filmId, long userId);

    List<Genre> getGenres();

    Genre getGenreById(int genreId);

    List<Mpa> getMpaRatings();

    Mpa getMpaById(int mpaId);

    List<Film> getSortedDirectorFilms(int directorId, String sortBy);

    void insertDirectorToFilm(int filmId, int directorId);

    void deleteFilmById(int id);
}