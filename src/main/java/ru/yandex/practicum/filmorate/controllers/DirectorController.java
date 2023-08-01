package ru.yandex.practicum.filmorate.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.DirectorDto;
import ru.yandex.practicum.filmorate.model.film.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/directors")
public class DirectorController {

    private final DirectorService directorService;
    @Autowired
    public DirectorController(DirectorService directorService) {
        this.directorService = directorService;
    }

    @GetMapping()
    public List<DirectorDto> getAllDirectors() {
        return directorService.getAllDirectors();
    }

    @GetMapping("/{id}")
    public DirectorDto getDirectorById(@PathVariable("id") int directorId) {
        return directorService.getDirectorById(directorId);
    }

    @PostMapping()
    public DirectorDto createDirector(@RequestBody @Valid DirectorDto directorDto) {
        return directorService.createDirector(directorDto);
    }

    @PutMapping()
    public DirectorDto updateDirector(@RequestBody DirectorDto directorDto) {
        return directorService.updateDirector(directorDto);
    }

    @DeleteMapping("/{id}")
    public void deleteDirector(@PathVariable("id") int directorId) {
        directorService.deleteDirector(directorId);
    }
}
