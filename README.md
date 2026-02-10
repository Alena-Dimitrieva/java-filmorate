# java-filmorate
Template repository for Filmorate project.

## ER-диаграмма:
![ER-диаграмма](/src/main/resources/er-diagram.png)

## **Описание таблиц**

#### users

Хранит всех пользователей приложения.

**id** — уникальный идентификатор пользователя.

**email** — e-mail пользователя.

**login** — логин пользователя.

**name** — имя пользователя (может быть NULL, в сервисе заменяется на login).

**irthday** — дата рождения пользователя.

### mpa

Справочник возрастных рейтингов MPA.

**id** — идентификатор рейтинга.

**name** — название рейтинга (G, PG, PG-13, R, NC-17).

### genres

Справочник жанров фильмов.

**id** — идентификатор жанра.

**name** — название жанра (Комедия, Драма, Мультфильм и т.д.).

### films

Хранит информацию о фильмах.

**id** — уникальный идентификатор фильма.

name — название фильма.

**description** — описание фильма.

**release_date** — дата релиза (не раньше 28.12.1895).

**duration** — продолжительность фильма в минутах.

**mpa_rating_id** — ссылка на рейтинг фильма (mpa.id).

### film_genres

Связь многие-ко-многим между фильмами и жанрами.

**film_id** — внешний ключ на films.id.

**genre_id** — внешний ключ на genres.id.

### film_likes

Связь многие-ко-многим между фильмами и пользователями (лайки).

**film_id** — внешний ключ на films.id.

**user_id** — внешний ключ на users.id.

### user_friends

Связь многие-ко-многим для пользователей с указанием статуса дружбы.

**user_id** — пользователь, инициирующий связь.

**friend_id** — второй пользователь.

**status** — статус дружбы (REQUESTED, CONFIRMED).

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

 Получение фильма с его жанрами
SELECT f.name AS film_name, fg.genre
FROM films f
JOIN film_genres fg ON f.id = fg.film_id
WHERE f.id = 1;

 Получение жанров фильма
SELECT fg.genre
FROM film_genres fg
WHERE fg.film_id = 1;

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

 Получение всех фильмов(доп. информация получается отдельным запросом)
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

