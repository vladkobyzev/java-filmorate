package ru.yandex.practicum.filmorate.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.service.FilmServiceImpl;
import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping("/films")
public class FilmsController {
    private final FilmServiceImpl filmServiceImpl;
    @Autowired
    public FilmsController(FilmServiceImpl filmServiceImpl) {
        this.filmServiceImpl = filmServiceImpl;
    }


    @GetMapping("/{id}")
    public Film getFilm(@PathVariable("id") int filmId) {
        return filmServiceImpl.getFilm(filmId);
    }

    @GetMapping
    public List<Film> getAllFilms() {
        return filmServiceImpl.getAllFilms();
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(required = false, defaultValue = "10") String count) {
        return filmServiceImpl.getPopularFilms(Integer.parseInt(count));
    }

    @GetMapping("/director/{directorId}")
    public List<Film> getSortedDirectorFilms(@PathVariable int directorId, @RequestParam String sortBy) {
        return filmServiceImpl.getSortedDirectorFilms(directorId, sortBy);
    }



    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        return filmServiceImpl.create(buildFilm(film));
    }

    @PutMapping()
    public Film updateFilm(@Valid @RequestBody Film film) {
        return filmServiceImpl.update(buildFilm(film), film.getId());
    }

    @PutMapping("/{id}/like/{userId}")
    public void like(@PathVariable("id") int filmId, @PathVariable int userId) {
        filmServiceImpl.like(filmId, userId);
    }


    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable("id") int filmId, @PathVariable int userId) {
        filmServiceImpl.deleteLike(filmId, userId);
    }

    @DeleteMapping("/{id}")
    public void deleteFilmById(@PathVariable int id) {
        filmServiceImpl.deleteFilmById(id);
    }

    private Film buildFilm(Film film) {
        return Film.builder()
                .id(film.getId())
                .name(film.getName())
                .description(film.getDescription())
                .releaseDate(film.getReleaseDate())
                .duration(film.getDuration())
                .mpa(film.getMpa())
                .genres(film.getGenres())
                .directors(film.getDirectors())
                .build();
    }
}
