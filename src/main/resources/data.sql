INSERT INTO genres (name, description, created_date, edited_date)
SELECT *
FROM (VALUES ('fantasy', 'Magic, worlds, mythical creatures', NOW(), NOW()),
             ('romance', 'Love stories and relationships', NOW(), NOW()),
             ('dystopian', 'Broken societies and survival', NOW(), NOW()),
             ('science fiction', 'Futuristic science themes', NOW(),
              NOW())) AS v(name, description, created_date, edited_date)
WHERE NOT EXISTS (SELECT 1 FROM genres);

INSERT INTO authors (full_name, deleted_date, created_date, edited_date)
SELECT *
FROM (VALUES ('Sarah J. Maas', NULL::date, NOW(), NOW()),
             ('Suzanne Collins', NULL::date, NOW(), NOW()),
             ('Ali Hazelwood', NULL::date, NOW(), NOW()),
             ('Rebecca Yarros', NULL::date, NOW(), NOW())) AS v(full_name, deleted_date, created_date, edited_date)
WHERE NOT EXISTS (SELECT 1 FROM authors);

INSERT INTO book_series (title, description, created_date, edited_date)
SELECT *
FROM (VALUES ('A Court of Thorns and Roses', 'Fae romance saga', NOW(), NOW()),
             ('The Hunger Games', 'Dystopian survival trilogy', NOW(), NOW()),
             ('Empyrean', 'Dragon rider war college', NOW(), NOW())) AS v(title, description, created_date, edited_date)
WHERE NOT EXISTS (SELECT 1 FROM book_series);

INSERT INTO books (title, isbn, release_date, description, image_url, average_rating, rating_count, series_id,
                   series_order, deleted_date, created_date, edited_date)
SELECT *
FROM (VALUES ('A Court of Thorns and Roses', '9781619634442', DATE '2015-05-05', 'Fae fantasy romance', NULL, 4.6, 1200,
              (SELECT id FROM book_series WHERE title = 'A Court of Thorns and Roses'), 1.0, NULL::date, NOW(), NOW()),
             ('A Court of Mist and Fury', '9781619634466', DATE '2016-05-03', 'Sequel in the ACOTAR series', NULL, 4.7,
              980,
              (SELECT id FROM book_series WHERE title = 'A Court of Thorns and Roses'), 2.0, NULL::date, NOW(), NOW()),
             ('The Hunger Games', '9780439023528', DATE '2008-09-14', 'Teens fight to survive in dystopia', NULL, 4.5,
              2100,
              (SELECT id FROM book_series WHERE title = 'The Hunger Games'), 1.0, NULL::date, NOW(), NOW()),
             ('Catching Fire', '9780439023498', DATE '2009-09-01', 'Second Hunger Games book', NULL, 4.4, 1850,
              (SELECT id FROM book_series WHERE title = 'The Hunger Games'), 2.0, NULL::date, NOW(), NOW()),
             ('The Love Hypothesis', '9780593336823', DATE '2021-09-14', 'STEM fake dating romance', NULL, 4.2, 760,
              NULL, NULL, NULL::date, NOW(), NOW()),
             ('Fourth Wing', '9781649374042', DATE '2023-04-05', 'Dragon riding war college', NULL, 4.6, 1500,
              (SELECT id FROM book_series WHERE title = 'Empyrean'), 1.0, NULL::date, NOW(), NOW())) AS v(title, isbn,
                                                                                                          release_date,
                                                                                                          description,
                                                                                                          image_url,
                                                                                                          average_rating,
                                                                                                          rating_count,
                                                                                                          series_id,
                                                                                                          series_order,
                                                                                                          deleted_date,
                                                                                                          created_date,
                                                                                                          edited_date)
WHERE NOT EXISTS (SELECT 1 FROM books);

INSERT INTO book_authors (book_id, author_id)
SELECT *
FROM (VALUES ((SELECT id FROM books WHERE isbn = '9781619634442'),
              (SELECT id FROM authors WHERE full_name = 'Sarah J. Maas')),
             ((SELECT id FROM books WHERE isbn = '9781619634466'),
              (SELECT id FROM authors WHERE full_name = 'Sarah J. Maas')),
             ((SELECT id FROM books WHERE isbn = '9780439023528'),
              (SELECT id FROM authors WHERE full_name = 'Suzanne Collins')),
             ((SELECT id FROM books WHERE isbn = '9780439023498'),
              (SELECT id FROM authors WHERE full_name = 'Suzanne Collins')),
             ((SELECT id FROM books WHERE isbn = '9780593336823'),
              (SELECT id FROM authors WHERE full_name = 'Ali Hazelwood')),
             ((SELECT id FROM books WHERE isbn = '9781649374042'),
              (SELECT id FROM authors WHERE full_name = 'Rebecca Yarros'))) AS v(book_id, author_id)
WHERE NOT EXISTS (SELECT 1 FROM book_authors);

INSERT INTO book_genres (book_id, genre_id)
SELECT *
FROM (VALUES ((SELECT id FROM books WHERE isbn = '9781619634442'), (SELECT id FROM genres WHERE name = 'fantasy')),
             ((SELECT id FROM books WHERE isbn = '9781619634442'), (SELECT id FROM genres WHERE name = 'romance')),
             ((SELECT id FROM books WHERE isbn = '9781619634466'), (SELECT id FROM genres WHERE name = 'fantasy')),
             ((SELECT id FROM books WHERE isbn = '9781619634466'), (SELECT id FROM genres WHERE name = 'romance')),
             ((SELECT id FROM books WHERE isbn = '9780439023528'), (SELECT id FROM genres WHERE name = 'dystopian')),
             ((SELECT id FROM books WHERE isbn = '9780439023498'), (SELECT id FROM genres WHERE name = 'dystopian')),
             ((SELECT id FROM books WHERE isbn = '9780593336823'), (SELECT id FROM genres WHERE name = 'romance')),
             ((SELECT id FROM books WHERE isbn = '9781649374042'),
              (SELECT id FROM genres WHERE name = 'fantasy'))) AS v(book_id, genre_id)
WHERE NOT EXISTS (SELECT 1 FROM book_genres);

INSERT INTO users (kcid, username, email, deleted_date, profile_picture_url, bio, created_date, edited_date)
SELECT *
FROM (VALUES ('add050d4-f322-446b-ba0b-b0e56edf1c14', 'admin-test', 'admin@test.com', NULL::date, NULL, NULL, NOW(),
              NOW()),
             ('c0cfba42-335d-4ac1-a1bf-3bc6b5091a9e', 'mod-test', 'mod@test.com', NULL::date, NULL, NULL, NOW(), NOW()),
             ('a2a554c6-c851-48c3-b769-0b223889312b', 'member-test', 'member@test.com', NULL::date, NULL, NULL, NOW(),
              NOW())) AS v(kcid, username, email, deleted_date, profile_picture_url, bio, created_date, edited_date)
WHERE NOT EXISTS (SELECT 1 FROM users);

INSERT INTO lists (name, description, user_id, required, created_date, edited_date)
SELECT *
FROM (VALUES ('Want to Read', NULL, (SELECT id FROM users WHERE username = 'member-test'), true, NOW(), NOW()),
             ('Currently Reading', NULL, (SELECT id FROM users WHERE username = 'member-test'), true, NOW(), NOW()),
             ('Finished', NULL, (SELECT id FROM users WHERE username = 'member-test'), true, NOW(), NOW()),
             ('Did Not Finish', NULL, (SELECT id FROM users WHERE username = 'member-test'), true, NOW(), NOW()),
             ('Fantasy Faves', 'My top fantasy books', (SELECT id FROM users WHERE username = 'member-test'), false,
              NOW(), NOW())) AS v(name, description, user_id, required, created_date, edited_date)
WHERE NOT EXISTS (SELECT 1 FROM lists);

INSERT INTO ratings (score, review, book_id, user_id, created_date, edited_date)
SELECT *
FROM (VALUES (5, 'Obsessed.', (SELECT id FROM books WHERE isbn = '9781619634442'),
              (SELECT id FROM users WHERE username = 'member-test'), NOW(), NOW()),
             (4, 'Loved the tension.', (SELECT id FROM books WHERE isbn = '9781649374042'),
              (SELECT id FROM users WHERE username = 'member-test'), NOW(),
              NOW())) AS v(score, review, book_id, user_id, created_date, edited_date)
WHERE NOT EXISTS (SELECT 1 FROM ratings);

INSERT INTO list_books (list_id, book_id)
SELECT *
FROM (VALUES ((SELECT id FROM lists WHERE name = 'Fantasy Faves'), (SELECT id FROM books WHERE isbn = '9781619634442')),
             ((SELECT id FROM lists WHERE name = 'Fantasy Faves'), (SELECT id FROM books WHERE isbn = '9781649374042')),
             ((SELECT id FROM lists WHERE name = 'Want to Read'), (SELECT id FROM books WHERE isbn = '9781619634466')),
             ((SELECT id FROM lists WHERE name = 'Want to Read'),
              (SELECT id FROM books WHERE isbn = '9780593336823'))) AS v(list_id, book_id)
WHERE NOT EXISTS (SELECT 1 FROM list_books);