# java-filmorate
Template repository for Filmorate project.

## ER-диаграмма:
![ER-диаграмма](/src/main/resources/er-diagram.png)

## **Описание таблиц**

- **users** — хранит всех пользователей приложения.
    - `id` — уникальный идентификатор пользователя.
    - `email` — e-mail пользователя.
    - `login` — логин пользователя.
    - `name` — имя пользователя (может быть пустым, в сервисе заменяется на login).
    - `birthday` — дата рождения пользователя.

- **films** — хранит все фильмы.
    - `id` — уникальный идентификатор фильма.
    - `name` — название фильма.
    - `description` — описание фильма (максимум 200 символов).
    - `release_date` — дата релиза фильма (не раньше 28.12.1895).
    - `duration` — продолжительность фильма в минутах.
    - `mpa_rating` — рейтинг MPA (`G`, `PG`, `PG-13`, `R`, `NC-17`).

- **film_genres** — связь многие-ко-многим для фильмов и их жанров.
    - `film_id` — внешний ключ на `films.id`.
    - `genre` — жанр фильма (`COMEDY`, `DRAMA`, `CARTOON`, `THRILLER`, `DOCUMENTARY`, `ACTION`).

- **film_likes** — связь многие-ко-многим для лайков пользователей.
    - `film_id` — внешний ключ на `films.id`.
    - `user_id` — внешний ключ на `users.id`.

- **user_friends** — связь многие-ко-многим для друзей с указанием статуса дружбы.
    - `user_id` — внешний ключ на `users.id`.
    - `friend_id` — внешний ключ на `users.id`.
    - `status` — статус дружбы (`UNCONFIRMED` или `CONFIRMED`).

## Примеры запросов

```sql
 Добавление пользователя
INSERT INTO users (email, login, name, birthday)
VALUES ('user1@example.com', 'user1', 'Алена', '1990-05-12');

 Добавление фильма
INSERT INTO films (name, description, release_date, duration, mpa_rating)
VALUES ('Интерстеллар', 'Научно-фантастический фильм', '2014-11-07', 169, 'PG-13');

 Добавление жанра к фильму
INSERT INTO film_genres (film_id, genre)
VALUES (1, 'SCI-FI');

 Добавление лайка фильму
INSERT INTO film_likes (film_id, user_id)
VALUES (1, 1);

 Удаление лайка
DELETE FROM film_likes
WHERE film_id = 1 AND user_id = 1;

 Получение количества лайков у фильма
SELECT COUNT(*) AS likes_count
FROM film_likes
WHERE film_id = 1;

 Добавление дружбы (запрос отправлен)
INSERT INTO user_friends (user_id, friend_id, status)
VALUES (1, 2, 'UNCONFIRMED');

 Подтверждение дружбы
UPDATE user_friends
SET status = 'CONFIRMED'
WHERE user_id = 1 AND friend_id = 2;

 Получение пользователя (друзья получаются отдельным запросом)
SELECT * FROM users WHERE id = 1;

 Получение друзей пользователя
SELECT u.*
FROM users u
JOIN user_friends uf ON u.id = uf.friend_id
WHERE uf.user_id = 1 AND uf.status = 'CONFIRMED';

 Получение всех фильмов
SELECT * FROM films;

 Получение топ-5 популярных фильмов
SELECT f.id, f.name, COUNT(l.user_id) AS likes_count
FROM films f
LEFT JOIN film_likes l ON f.id = l.film_id
GROUP BY f.id
ORDER BY likes_count DESC
LIMIT 5;

 Получение списка друзей пользователя
SELECT u.*
FROM users u
JOIN user_friends uf ON u.id = uf.friend_id
WHERE uf.user_id = 1 AND uf.status = 'CONFIRMED';

 Получение общих друзей двух пользователей
SELECT u.*
FROM users u
JOIN user_friends uf1 ON u.id = uf1.friend_id
JOIN user_friends uf2 ON u.id = uf2.friend_id
WHERE uf1.user_id = 1 AND uf2.user_id = 2
  AND uf1.status = 'CONFIRMED'
  AND uf2.status = 'CONFIRMED';

