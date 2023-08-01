package ru.yandex.practicum.filmorate.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
@Data
public class DirectorDto {
    private int id;
    @NotBlank
    private String name;
}
