drop table if exists Review_User, Reviews, Friends, Invites, Likes, Users, Mpa, Directors, Film_Genre, Genres, Films;
CREATE TABLE IF NOT EXISTS Mpa (
    mpa_id   INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    mpa_name varchar
);

CREATE TABLE IF NOT EXISTS Directors (
    director_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    director_name varchar(100)
);

CREATE TABLE IF NOT EXISTS Films (
    film_id     INTEGER GENERATED BY DEFAULT AS IDENTITY Primary Key NOT NULL,
    name        varchar,
    description varchar,
    releaseDate date,
    duration    int,
    mpa_id      INTEGER REFERENCES Mpa (mpa_id),
    director_id INTEGER REFERENCES Directors(director_id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS Users (
    user_id  INTEGER GENERATED BY DEFAULT AS IDENTITY Primary Key,
    email    varchar,
    login    varchar,
    name     varchar,
    birthday date
);

CREATE TABLE IF NOT EXISTS Likes (
    film_id INTEGER REFERENCES Films (film_id) ON DELETE CASCADE,
    user_id INTEGER REFERENCES Users (user_id) ON DELETE CASCADE,
    PRIMARY KEY (film_id, user_id)
);

CREATE TABLE IF NOT EXISTS Genres (
    genre_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name     varchar
);

CREATE TABLE IF NOT EXISTS Film_Genre (
    film_id  INTEGER REFERENCES Films (film_id) ON DELETE CASCADE,
    genre_id INTEGER REFERENCES Genres (genre_id),
    UNIQUE(film_id, genre_id)
);

CREATE TABLE IF NOT EXISTS Friends (
    user_id   INTEGER REFERENCES Users (user_id) ON DELETE CASCADE,
    friend_id INTEGER REFERENCES Users (user_id)
);

CREATE TABLE IF NOT EXISTS Invites (
    invite_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    from_id   INTEGER REFERENCES Users (user_id) ON DELETE CASCADE,
    to_id     INTEGER REFERENCES Users (user_id) ON DELETE CASCADE,
    status    varchar
);

CREATE TABLE IF NOT EXISTS Reviews (
    review_id   INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    content     varchar(200),
    is_positive boolean,
    user_id     INTEGER REFERENCES Users (user_id) ON DELETE CASCADE,
    film_id     INTEGER REFERENCES Films (film_id) ON DELETE CASCADE,
    useful      int DEFAULT 0
);

CREATE TABLE IF NOT EXISTS Review_User (
    review_id  INTEGER REFERENCES Reviews (review_id) ON DELETE CASCADE,
    user_id    INTEGER REFERENCES Users (user_id) ON DELETE CASCADE,
    assessment varchar
);


