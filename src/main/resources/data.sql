DROP TABLE IF EXISTS users CASCADE;
CREATE TABLE users
(
    id            SERIAL PRIMARY KEY,
    username      TEXT UNIQUE NOT NULL,
    email         TEXT        NOT NULL DEFAULT 'test@example.com',
    password_hash TEXT        NOT NULL,
    type          TEXT        NOT NULL,
    signup_time   TIMESTAMP   NOT NULL
);
DROP TABLE IF EXISTS books CASCADE;
CREATE TABLE books
(
    id          SERIAL PRIMARY KEY,
    isbn        TEXT NOT NULL,
    title       TEXT NOT NULL,
    author      TEXT NOT NULL,
    description TEXT,
    price       NUMERIC,
    location    TEXT NOT NULL
);
CREATE INDEX ON books (isbn);
DROP TABLE IF EXISTS lending_log;
CREATE TABLE lending_log
(
    id         SERIAL PRIMARY KEY,
    book_id    INTEGER   NOT NULL REFERENCES books (id),
    user_id    INTEGER   NOT NULL REFERENCES users (id),
    start_time TIMESTAMP NOT NULL,
    end_time   TIMESTAMP
);
CREATE INDEX ON lending_log (book_id);
CREATE INDEX ON lending_log (user_id);
DROP TABLE IF EXISTS action_log;
CREATE TABLE action_log
(
    id      SERIAL PRIMARY KEY,
    user_id INTEGER   NOT NULL REFERENCES users (id),
    type    TEXT      NOT NULL,
    info    JSONB,
    time    TIMESTAMP NOT NULL
);
CREATE INDEX ON action_log (user_id);
CREATE INDEX ON action_log (type);
DROP TABLE IF EXISTS categories CASCADE;
CREATE TABLE categories
(
    id                 SERIAL PRIMARY KEY,
    name               TEXT NOT NULL,
    parent_category_id INTEGER REFERENCES categories (id)
);
CREATE INDEX ON categories (name);
CREATE INDEX ON categories (parent_category_id);
DROP TABLE IF EXISTS book_category_rel;
CREATE TABLE book_category_rel
(
    book_id     INTEGER NOT NULL,
    category_id INTEGER REFERENCES categories (id),
    PRIMARY KEY (book_id, category_id)
);
CREATE INDEX ON book_category_rel (category_id);
DROP TABLE IF EXISTS reservations;
CREATE TABLE reservations
(
    id      SERIAL PRIMARY KEY,
    book_id INTEGER   NOT NULL REFERENCES books (id),
    user_id INTEGER   NOT NULL REFERENCES users (id),
    time    TIMESTAMP NOT NULL
);
CREATE INDEX ON reservations (book_id);
CREATE INDEX ON reservations (user_id);
CREATE INDEX ON reservations (time);
DROP TABLE IF EXISTS configuration;
CREATE TABLE configuration
(
    key   TEXT PRIMARY KEY,
    value TEXT NOT NULL
);
DROP TABLE IF EXISTS news;
CREATE TABLE news
(
id SERIAL PRIMARY KEY,
title TEXT NOT NULL,
content TEXT NOT NULL,
time    TIMESTAMP NOT NULL DEFAULT now(),
poster_id INTEGER NOT NULL REFERENCES users(id)
);
INSERT INTO users (username, email, password_hash, type, signup_time)
VALUES ('admin', 'admin@local.invalid',
        '$argon2id$v=19$m=65536,t=2,p=1$Awz7DXJOmoT4/DwNauoyjQ$geMYnip4NmWsxe7eukKOpps+bdOc7doefLm0480E0tY',
        'Admin', '2019-09-20 22:29:55.670000');
INSERT INTO users (username, email, password_hash, type, signup_time)
VALUES ('reader', 'reader@local.invalid',
        '$argon2id$v=19$m=65536,t=2,p=1$M3j4icn1Le+EB6IeZ/7y7Q$T688ZmXe7nwkdULHMrYehB2vjTsOfU1nLGHa4sa0PUw', 'Reader',
        '2019-09-20 22:30:06.591000');
INSERT INTO users (username, email, password_hash, type, signup_time)
VALUES ('librarian', 'librarian@local.invalid',
        '$argon2id$v=19$m=65536,t=2,p=1$Db0a+QNTy9PZiOMeU1k5fA$dw/ibar4JiBHkZI1qamRINjdu8H2CEmW9aUL5dDZTSw',
        'Librarian', '2019-09-20 22:30:17.050000');