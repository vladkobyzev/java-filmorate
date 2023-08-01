package ru.yandex.practicum.filmorate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.film.Director;

@Repository
public interface DirectorRepository extends JpaRepository<Director, Integer> {
    Director findById(int directorId);
}
