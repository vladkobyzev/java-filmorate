package ru.yandex.practicum.filmorate.Util;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.film.Director;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DirectorMapper implements RowMapper<Director> {
    @Override
    public Director mapRow(ResultSet rs, int rowNum) throws SQLException {
        Director director = new Director();
        director.setId(rs.getInt("director_id"));
        director.setName(rs.getString("director_name"));
        return director;
    }
}
