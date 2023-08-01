package ru.yandex.practicum.filmorate.model.film;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import ru.yandex.practicum.filmorate.exception_handler.exceptions.ValidationException;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Data
@Builder
public class Film {
    private int id;

    @NotBlank
    private String name;

    @Size(max = 200)
    private String description;

    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;

    @Positive
    private int duration;
    private int rate;
    private Mpa mpa;
    @Builder.Default
    private List<Genre> genres = new ArrayList<>();
    @Builder.Default
    private List<Director> directors = new ArrayList<>();

    public static class FilmBuilder {
        private LocalDate releaseDate;
        public FilmBuilder releaseDate(LocalDate releaseDate) {
            if(releaseDate.isBefore(LocalDate.of(1895,12,28))) {
                throw new ValidationException("Impossible release date");
            } else {
                this.releaseDate = releaseDate;
            }
            return this;
        }

//        private Director director;
//
//        public FilmBuilder director(Director director) {
//            if(director == null) {
//                this.director = new Director();
//            }
//            return this;
//        }
    }
}
