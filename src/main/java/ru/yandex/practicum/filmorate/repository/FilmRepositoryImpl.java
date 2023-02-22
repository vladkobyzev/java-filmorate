package ru.yandex.practicum.filmorate.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.Util.DirectorMapper;
import ru.yandex.practicum.filmorate.Util.FilmMapper;
import ru.yandex.practicum.filmorate.Util.GenreMapper;
import ru.yandex.practicum.filmorate.Util.MpaMapper;
import ru.yandex.practicum.filmorate.model.film.*;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Repository
public class FilmRepositoryImpl implements FilmRepository {
    private static final String ALL_FILMS_SQL_QUERY = "SELECT * FROM Films " +
            "JOIN Mpa ON Films.mpa_id=Mpa.mpa_id ";
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Film getFilm(int id) throws EmptyResultDataAccessException {
        Film film = jdbcTemplate.queryForObject(ALL_FILMS_SQL_QUERY + "WHERE film_id = ?",
                new FilmMapper(), id);
        film.setGenres(getAllFilmsGenres(id));
        film.setDirectors(getDirectors(id));
        return film;
    }

    @Override
    public List<Film> getAllFilms() {
        List<Film> filmsWithoutGenres = jdbcTemplate.query(ALL_FILMS_SQL_QUERY, new FilmMapper());

        List<Film> filmsWithGenres = addGenresInFilm(filmsWithoutGenres);
        return addDirectorToAllFilms(filmsWithGenres);
    }

    @Override
    public List<Film> getPopularFilms(int countTopFilms) {
        final String getTopFilmsQuery = ALL_FILMS_SQL_QUERY +
                "WHERE film_id IN (SELECT film_id FROM Likes GROUP BY film_id ORDER BY COUNT(user_id) DESC LIMIT ?)";
        List<Film> popularFilms = jdbcTemplate.query(getTopFilmsQuery, new FilmMapper(), countTopFilms);

        if (popularFilms.size() < countTopFilms) {
            List<Film> additionalFilms = getAllFilms();
            popularFilms.removeAll(additionalFilms);
            popularFilms.addAll(additionalFilms);
        }
        return popularFilms;
    }

    @Override
    public List<Film> getMostPopulars(int limit, int genreId, int year) {
        String getMostPopularsQuery = ALL_FILMS_SQL_QUERY;
        if (genreId != 0 & year == 0) {
            getMostPopularsQuery += "WHERE film_id IN (SELECT film_id FROM Film_Genre WHERE genre_id = ?)";
            List<Film> mostPopularFilms = jdbcTemplate.query(getMostPopularsQuery, new FilmMapper(), genreId);
            return addGenresInFilm(mostPopularFilms);
        } else if (genreId == 0 & year != 0) {
            String startYear = year + "-01-01";
            String endYear = year + "-12-31";
            getMostPopularsQuery += "WHERE releaseDate BETWEEN DATE '" + startYear + "' AND DATE '" + endYear + "'";
            List<Film> mostPopularFilms = jdbcTemplate.query(getMostPopularsQuery, new FilmMapper());
            return addGenresInFilm(mostPopularFilms);
        } else {
            String startYear = year + "-01-01";
            String endYear = year + "-12-31";
            getMostPopularsQuery +=
                    "WHERE film_id IN (SELECT film_id FROM Film_Genre WHERE genre_id = ?) " +
                            "AND " + "releaseDate BETWEEN DATE '" + startYear + "' AND DATE '" + endYear + "'";

            List<Film> mostPopularFilms = jdbcTemplate.query(getMostPopularsQuery, new FilmMapper(), genreId);
            return addGenresInFilm(mostPopularFilms);
        }
    }

    @Override
    public List<Film> getCommonFilms(int userId, int friendId) {
        String sql = "SELECT f.*, M.* " +
                "FROM LIKES " +
                "JOIN LIKES l ON l.FILM_ID = LIKES.FILM_ID " +
                "JOIN FILMS f on f.film_id = l.film_id " +
                "JOIN MPA M on f.mpa_id = M.MPA_ID " +
                "WHERE l.USER_ID = ? AND LIKES.USER_ID = ?";

        return jdbcTemplate.query(sql, new FilmMapper(), userId, friendId);
    }

    @Override
    public int createFilm(Film film) {
        int filmId = insertFilm(film);

        log.info("Film added: " + film.getName());

        if(film.getDirectors() != null) {
            insertDirectorToFilm(filmId, film.getDirectors().get(0).getId());
        }
        insertFilmsGenres(film, filmId);

        return filmId;
    }

    @Override
    public Film update(Film film, int filmId) {
        final String updateQuery = "UPDATE Films SET name=?, description=?, releaseDate=?, duration=?, mpa_id=? WHERE film_id=?";
        jdbcTemplate.update(updateQuery,
                film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration(), film.getMpa().getId(), filmId);
        log.info("Film updated: " + film.getName());

        Film filmWithDirector = updateFilmDirector(film, filmId);
        updateFilmsGenre(filmWithDirector, filmId);
        return getFilm(filmId);
    }

    @Override
    public void deleteFilmById(int id) {
        Film film = getFilm(id);
        //удаление отзывов!!!!!!!!!!!

        String deleteQuery = "DELETE FROM likes WHERE film_id=?";
        jdbcTemplate.update(deleteQuery, id);

        final String genresSqlQuery = "DELETE FROM film_genre WHERE FILM_ID = ?";
        jdbcTemplate.update(genresSqlQuery, id);
        final String sqlQuery = "DELETE FROM films WHERE FILM_ID = ?";
        jdbcTemplate.update(sqlQuery, id);
    }

    public boolean isFilmExist(int filmId) {
        String sql = "SELECT COUNT(*) FROM Films where film_id=?";

        int count = jdbcTemplate.queryForObject(sql,
                new Object[] { filmId }, Integer.class);

        if (count >= 1)
        {
            return true;
        }
        return false;
    }


    @Override
    public void like(int filmId, int userId) {
        String likeQuery = "INSERT INTO Likes(film_id, user_id) VALUES (?, ?)";
        increaseFilmRate(filmId);
        jdbcTemplate.update(likeQuery, filmId, userId);
    }

    public int deleteLike(int filmId, long userId) {
        String deleteQuery = "DELETE FROM Likes WHERE EXISTS(SELECT 1 FROM LIKES WHERE film_id=? AND user_id=?)";
        decreaseFilmRate(filmId);
        return jdbcTemplate.update(deleteQuery, filmId, userId);
    }

    public boolean increaseFilmRate(int filmId) {
        String sqlQuery = "UPDATE FILMS SET rate = rate + 1 WHERE film_id=?";
        return jdbcTemplate.update(sqlQuery, filmId) > 0;
    }
    public boolean decreaseFilmRate(int filmId) {
        String sqlQuery = "UPDATE FILMS SET rate = rate - 1 WHERE film_id=?";
        return jdbcTemplate.update(sqlQuery, filmId) > 0;
    }


    @Override
    public List<Genre> getGenres() {
        return jdbcTemplate.query("SELECT * FROM Genres", new GenreMapper());
    }

    @Override
    public Genre getGenreById(int genreId) {
        return jdbcTemplate.queryForObject("SELECT * FROM Genres WHERE genre_id=?",
                new GenreMapper(), genreId);
    }


    @Override
    public List<Mpa> getMpaRatings() {
        return jdbcTemplate.query("SELECT * FROM Mpa", new MpaMapper());
    }

    @Override
    public Mpa getMpaById(int mpaId) {
        return jdbcTemplate.queryForObject("SELECT * FROM Mpa WHERE mpa_id=?",
                new MpaMapper(), mpaId);
    }

    @Override
    public List<Film> searchFilms() {
        final String sql = "SELECT * " +
                "FROM FILMS f " +
                "INNER JOIN MPA m ON f.mpa_id = m.mpa_id " +
                "WHERE f.film_id IN " +
                "(SELECT film_id FROM LIKES GROUP BY film_id ORDER BY COUNT(user_id) DESC) ";
        List<Film> searchFilms = addGenresInFilm(jdbcTemplate.query(sql, new FilmMapper()));
        return addDirectorToAllFilms(searchFilms);
    }

    @Override
    public List<Film> searchFilmsByDirector(String query) {
        final String sql = "SELECT * " +
                "FROM FILMS f " +
                "INNER JOIN MPA m ON f.mpa_id = m.mpa_id  " +
                "INNER JOIN FILM_DIRECTOR fd ON f.FILM_ID  = fd.FILM_ID " +
                "INNER JOIN DIRECTORS d ON fd.DIRECTOR_ID = d.DIRECTOR_ID " +
                "WHERE LOWER(DIRECTOR_NAME) LIKE ?";
        List<Film> searchFilms = addGenresInFilm(jdbcTemplate.query(sql, new FilmMapper(), new String[] {"%" + query + "%"}));
        return addDirectorToAllFilms(searchFilms);
    }

    @Override
    public List<Film> searchFilmsByTitle(String query) {
        final String sql = "SELECT * " +
                "FROM FILMS f " +
                "INNER JOIN MPA m ON f.mpa_id = m.mpa_id " +
                "WHERE LOWER(NAME) LIKE ?";
        List<Film> searchFilms = addGenresInFilm(jdbcTemplate.query(sql, new FilmMapper(), new String[] {"%" + query + "%"}));
        return addDirectorToAllFilms(searchFilms);
    }

    @Override
    public List<Film> searchFilmsByDirectorAndTitle(String query) {
        final String sql = "SELECT f.FILM_ID, f.NAME, f.DESCRIPTION, f.RELEASEDATE, f.DURATION, f.RATE, f.MPA_ID, m.mpa_name  " +
                "FROM FILMS f " +
                "INNER JOIN MPA m ON f.mpa_id = m.mpa_id " +
                " WHERE  LOWER(NAME) LIKE ? " +
                "UNION " +
                "SELECT f.FILM_ID, f.NAME, f.DESCRIPTION, f.RELEASEDATE, f.DURATION, f.RATE, f.MPA_ID, m.mpa_name " +
                "FROM FILMS f " +
                "INNER JOIN MPA m ON f.mpa_id = m.mpa_id  " +
                "INNER JOIN FILM_DIRECTOR fd ON f.FILM_ID  = fd.FILM_ID " +
                "INNER JOIN DIRECTORS d ON fd.DIRECTOR_ID = d.DIRECTOR_ID  " +
                "WHERE LOWER(DIRECTOR_NAME) LIKE ? " +
                "ORDER BY RATE DESC;";
        List<Film> searchFilms = addGenresInFilm(jdbcTemplate.query(sql, new FilmMapper(), new String[] {"%" + query + "%","%" + query + "%"}));
        return addDirectorToAllFilms(searchFilms);
    }

    @Override
    public List<Film> getSortedDirectorFilms(int directorId, String sqlQuery) {
        List<Film> sortedDirectorFilms = jdbcTemplate.query(sqlQuery,
                new FilmMapper(), directorId);

        List<Film> sortedFilmsWithGenres = addGenresInFilm(sortedDirectorFilms);
        return addDirectorToAllFilms(sortedFilmsWithGenres);
    }

    @Override
    public void insertDirectorToFilm(int filmId, int directorId) {
        jdbcTemplate.update("INSERT INTO Film_Director (film_id, director_id) VALUES (?, ?)", filmId, directorId);
    }

    List<Genre> getAllFilmsGenres(int filmId) {
        final String genresQuery =
                "SELECT * FROM Film_Genre JOIN Genres ON Film_Genre.genre_id=Genres.genre_id WHERE film_id = ?";
        return jdbcTemplate.query(genresQuery, new GenreMapper(), filmId);
    }

    private List<Director> getDirectors(int filmId) {
        return jdbcTemplate.query("SELECT * FROM FILM_DIRECTOR " +
                        "Join Directors ON FILM_DIRECTOR.DIRECTOR_ID = DIRECTORS.DIRECTOR_ID " +
                        "WHERE film_id=? AND DIRECTORS.DIRECTOR_ID IS NOT NULL",
                new DirectorMapper(), filmId);
    }

    private void insertFilmsGenres(Film film, int filmId) {
        final String insertGenres = "INSERT INTO Film_Genre VALUES(?, ?)";

        if (film.getGenres() != null) {
            List<Integer> genresId = film.getGenres().stream()
                    .map(Genre::getId)
                    .distinct()
                    .sorted()
                    .collect(Collectors.toList());
            deleteGenre(filmId);
            jdbcTemplate.batchUpdate(insertGenres, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ps.setInt(1, filmId);
                    ps.setInt(2, genresId.get(i));
                }

                @Override
                public int getBatchSize() {
                    return genresId.size();
                }
            });
        }
    }

    private int insertFilm(Film film) {
        final String insertSql = "INSERT INTO Films(name, description, releaseDate, duration, mpa_id) " +
                "VALUES (?, ?, ?, ?, ?)";

        jdbcTemplate.update(insertSql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId());

        return getInsertedFilmId();
    }

    private List<Film> addDirectorToAllFilms(List<Film> filmsWithGenres) {
        final String genreQuery = "SELECT film_id, DIRECTORS.director_id, DIRECTORS.director_name FROM FILM_DIRECTOR JOIN Directors ON FILM_DIRECTOR.director_id=Directors.director_id";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(genreQuery);

        for (Film film : filmsWithGenres) {
            List<Director> directors = rows.stream()
                    .filter(stringObjectMap -> (int) stringObjectMap.get("FILM_ID") == film.getId())
                    .map(stringObjectMap -> {
                        Director director = new Director();
                        director.setId((Integer) stringObjectMap.get("DIRECTOR_ID"));
                        director.setName((String) stringObjectMap.get("DIRECTOR_NAME"));
                        return director;
                    })
                    .collect(Collectors.toList());
            film.getDirectors().addAll(directors);
        }
        return filmsWithGenres;
    }

    private int getInsertedFilmId() {
        int filmId = 0;
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("SELECT film_id FROM Films ORDER BY film_id DESC LIMIT 1");
        if (filmRows.next()) {
            return Integer.parseInt(filmRows.getString("film_id"));
        }
        return filmId;
    }

    private Film updateFilmDirector(Film film, int filmId) {
        if(film.getDirectors() != null) {
            insertDirectorToFilm(filmId, film.getDirectors().get(0).getId());
            film.setDirectors(getDirectors(filmId));
        } else {
            jdbcTemplate.update("UPDATE FILM_DIRECTOR SET director_id = null WHERE film_id = ?", filmId);
        }
        return film;
    }


    private void updateFilmsGenre(Film film, int filmId) {
        if (film.getGenres() != null && film.getGenres().isEmpty()) {
            deleteGenre(filmId);
        } else {
            insertFilmsGenres(film, filmId);
        }
    }

    private void deleteGenre(int filmId) {
        String deleteGenreQuery = "DELETE FROM Film_Genre WHERE EXISTS(SELECT 1 FROM Film_Genre WHERE film_id=?)";
        jdbcTemplate.update(deleteGenreQuery, filmId);
    }

    private List<Film> addGenresInFilm(List<Film> films) {
        final String genreQuery = "SELECT * FROM Film_Genre JOIN Genres ON Film_Genre.genre_id=Genres.genre_id";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(genreQuery);

        for (Film film : films) {
            List<Genre> allFilmsGenres = rows.stream()
                    .filter(stringObjectMap -> (int)stringObjectMap.get("FILM_ID") == film.getId())
                    .map(stringObjectMap -> {
                        Genre genre = new Genre();
                        genre.setId((Integer) stringObjectMap.get("GENRE_ID"));
                        genre.setName((String) stringObjectMap.get("NAME"));
                        return genre;
                    })
                    .collect(Collectors.toList());
            film.setGenres(allFilmsGenres);
        }
        return films;
    }
}
