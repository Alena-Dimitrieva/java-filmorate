-- Инициализация рейтингов MPA
MERGE INTO mpa (id, name) KEY (id) VALUES
    (1, 'G'),
    (2, 'PG'),
    (3, 'PG-13'),
    (4, 'R'),
    (5, 'NC-17');

-- Инициализация жанров
MERGE INTO genres (id, name) KEY (id) VALUES
    (1, 'Комедия'),
    (2, 'Драма'),
    (3, 'Мультфильм'),
    (4, 'Триллер'),
    (5, 'Документальный'),
    (6, 'Боевик');

-- Инициализация пользователей
INSERT INTO users (email, login, name, birthday) VALUES
    ('user1@example.com', 'user1', 'Алена', '1990-05-12'),
    ('user2@example.com', 'user2', 'Иван', '1985-03-23'),
    ('user3@example.com', 'user3', 'Мария', '2000-11-01');

-- Инициализация фильмов
INSERT INTO films (name, description, release_date, duration, mpa_rating_id) VALUES
    ('Интерстеллар', 'Фантастический эпик о космических путешествиях.', '2014-11-07', 169, 3), -- PG-13
    ('Джентльмены', 'Боевик с юмором о криминальных разборках.', '2019-01-24', 113, 3),         -- PG-13
    ('Зеленая книга', 'Драма о дружбе и путешествии через США 60-х.', '2018-11-16', 130, 2);    -- PG

-- Присвоение жанров фильмам
MERGE INTO film_genres (film_id, genre_id) KEY (film_id, genre_id) VALUES
    (1, 4), -- Интерстеллар - Триллер
    (2, 6), -- Джентльмены - Боевик
    (2, 1), -- Джентльмены - Комедия
    (3, 2); -- Зеленая книга - Драма

-- Лайки фильмов пользователями (теперь MERGE, чтобы не падало на PK)
MERGE INTO film_likes (film_id, user_id) KEY (film_id, user_id) VALUES
    (1, 1),
    (1, 2),
    (2, 3),
    (3, 1);

-- Дружба пользователей (односторонняя)
MERGE INTO user_friends (user_id, friend_id, status) KEY (user_id, friend_id) VALUES
    (1, 2, 'CONFIRMED'),  -- Алена добавила Ивана
    (2, 3, 'REQUESTED');  -- Иван отправил запрос Марии