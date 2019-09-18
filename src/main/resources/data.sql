DROP TABLE IF EXISTS users CASCADE;
CREATE TABLE users
(
    id            SERIAL PRIMARY KEY,
    username      TEXT UNIQUE NOT NULL,
    password_hash TEXT        NOT NULL,
    type          TEXT        NOT NULL,
    signup_time   TIMESTAMP   NOT NULL
);
DROP TABLE IF EXISTS books CASCADE;
CREATE TABLE books
(
    id       SERIAL PRIMARY KEY,
    isbn     TEXT UNIQUE NOT NULL,
    title    TEXT        NOT NULL,
    author   TEXT        NOT NULL,
    location TEXT        NOT NULL,
    price    NUMERIC     NOT NULL
);
DROP TABLE IF EXISTS lending_log;
CREATE TABLE lending_log
(
    id         SERIAL PRIMARY KEY,
    book_id    INTEGER   NOT NULL REFERENCES books (id),
    user_id    INTEGER   NOT NULL REFERENCES users (id),
    start_time TIMESTAMP NOT NULL,
    end_time   TIMESTAMP
);
DROP TABLE IF EXISTS action_log;
CREATE TABLE action_log
(
    id      SERIAL PRIMARY KEY,
    user_id INTEGER   NOT NULL REFERENCES users (id),
    type    TEXT      NOT NULL,
    info    JSONB,
    time    TIMESTAMP NOT NULL
);
DROP TABLE IF EXISTS categories;
CREATE TABLE categories
(
    id SERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    parent_category_id INTEGER REFERENCES categories(id)
);
