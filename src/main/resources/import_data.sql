INSERT INTO users ( user_name,  email,  login,  birthday) values ('User_1', 'user1@email.com', 'login_1', '1970-10-23');
INSERT INTO users ( user_name,  email,  login,  birthday) values ('User_2', 'user2@email.com', 'login_2', '1970-10-23');
INSERT INTO users ( user_name,  email,  login,  birthday) values ('User_3', 'user3@email.com', 'login_3', '1970-10-23');
INSERT INTO users ( user_name,  email,  login,  birthday) values ('User_4', 'user4@email.com', 'login_4', '1970-10-23');
INSERT INTO users ( user_name,  email,  login,  birthday) values ('User_5', 'user5@email.com', 'login_5', '1970-10-23');

INSERT INTO films( film_name,  film_description,  release_date,  duration, mpa_id)
values ('Film_1', 'Description_1', '1970-10-23', '100', '1');
INSERT INTO films( film_name,  film_description,  release_date,  duration, mpa_id)
values ('Film_2', 'Description_2', '1970-10-23', '100', '2');
INSERT INTO films( film_name,  film_description,  release_date,  duration, mpa_id)
values ('Film_3', 'Description_3', '1970-10-23', '100', '1');
INSERT INTO films( film_name,  film_description,  release_date,  duration, mpa_id)
values ('Film_4', 'Description_4', '1970-10-23', '100', '4');
INSERT INTO films( film_name,  film_description,  release_date,  duration, mpa_id)
values ('Film_5', 'Description_5', '1970-10-23', '100', '1');

INSERT INTO films_marks( film_id, user_id, mark) values ('1', '1', '3');
INSERT INTO films_marks( film_id, user_id, mark) values ('1', '3', '3');
INSERT INTO films_marks( film_id, user_id, mark) values ('1', '4', '3');
INSERT INTO films_marks( film_id, user_id, mark) values ('1', '5', '9');
INSERT INTO films_marks( film_id, user_id, mark) values ('2', '1', '7');
INSERT INTO films_marks( film_id, user_id, mark) values ('2', '2', '7');
INSERT INTO films_marks( film_id, user_id, mark) values ('2', '3', '7');
INSERT INTO films_marks( film_id, user_id, mark) values ('2', '5', '2');
INSERT INTO films_marks( film_id, user_id, mark) values ('3', '2', '9');
INSERT INTO films_marks( film_id, user_id, mark) values ('3', '3', '6');
INSERT INTO films_marks( film_id, user_id, mark) values ('3', '4', '6');
INSERT INTO films_marks( film_id, user_id, mark) values ('4', '3', '9');
INSERT INTO films_marks( film_id, user_id, mark) values ('4', '5', '5');
INSERT INTO films_marks( film_id, user_id, mark) values ('5', '1', '2');
INSERT INTO films_marks( film_id, user_id, mark) values ('5', '2', '2');
INSERT INTO films_marks( film_id, user_id, mark) values ('5', '3', '2');
INSERT INTO films_marks( film_id, user_id, mark) values ('5', '4', '8');
INSERT INTO films_marks( film_id, user_id, mark) values ('5', '5', '4');

INSERT INTO directors( director_name) values ('Director_1');
INSERT INTO directors( director_name) values ('Director_2');

INSERT INTO film_directors( film_id, director_id) values ('1', '1');
INSERT INTO film_directors( film_id, director_id) values ('2', '2');
INSERT INTO film_directors( film_id, director_id) values ('3', '1');
INSERT INTO film_directors( film_id, director_id) values ('4', '2');
INSERT INTO film_directors( film_id, director_id) values ('5', '1');