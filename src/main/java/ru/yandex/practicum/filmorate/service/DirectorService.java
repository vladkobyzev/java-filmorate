package ru.yandex.practicum.filmorate.service;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.DirectorDto;
import ru.yandex.practicum.filmorate.exception_handler.exceptions.DirectorNotFound;
import ru.yandex.practicum.filmorate.model.film.Director;
import ru.yandex.practicum.filmorate.repository.DirectorRepository;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DirectorService {
    private final DirectorRepository directorRepository;
    private final ModelMapper modelMapper;

    public DirectorService(DirectorRepository directorRepository, ModelMapper modelMapper) {
        this.directorRepository = directorRepository;
        this.modelMapper = modelMapper;
    }

    public List<DirectorDto> getAllDirectors() {
        return convertDirectorsToDtoList(directorRepository.findAll());
    }

    public DirectorDto getDirectorById(int directorId) {
        isDirectorExist(directorId);
        return convertDirectorToDto(directorRepository.findById(directorId));
    }

    public DirectorDto createDirector(DirectorDto directorDto) {
        Director director = convertDtoToDirector(directorDto);
        return convertDirectorToDto(directorRepository.save(director));
    }

    public DirectorDto updateDirector(DirectorDto directorDto) {
        DirectorDto updatedDirector = getDirectorById(directorDto.getId());
        updatedDirector.setName(directorDto.getName());
        return createDirector(updatedDirector);
    }

    public void deleteDirector(int directorId) {
        isDirectorExist(directorId);
        directorRepository.deleteById(directorId);
    }

    void isDirectorExist(int directorId) {
        if(!directorRepository.existsById(directorId)) {
            throw new DirectorNotFound("Director id: " + directorId);
        }
    }

    private Director convertDtoToDirector(DirectorDto directorDto) {
        return this.modelMapper.map(directorDto, Director.class);
    }

    private DirectorDto convertDirectorToDto(Director director) {
        return this.modelMapper.map(director, DirectorDto.class);
    }

    private List<DirectorDto> convertDirectorsToDtoList(List<Director> reviews) {
        return reviews.stream()
                .map(review -> this.modelMapper.map(review, DirectorDto.class))
                .collect(Collectors.toList());
    }
}
