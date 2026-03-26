INSERT INTO genres (name, description, created_date, edited_date)
VALUES ('fantasy', 'Magic, worlds, mythical creatures', NOW(), NOW()),
       ('romance', 'Love stories and relationships', NOW(), NOW()),
       ('dystopian', 'Broken societies and survival', NOW(), NOW()),
       ('science fiction', 'Futuristic science themes', NOW(), NOW());

INSERT INTO authors (full_name, created_date, edited_date)
VALUES ('Sarah J. Maas', NOW(), NOW()),
       ('Suzanne Collins', NOW(), NOW()),
       ('Ali Hazelwood', NOW(), NOW()),
       ('Rebecca Yarros', NOW(), NOW());

INSERT INTO book_series (title, description, created_date, edited_date)
VALUES ('A Court of Thorns and Roses', 'Fae romance saga', NOW(), NOW()),
       ('The Hunger Games', 'Dystopian survival trilogy', NOW(), NOW()),
       ('Empyrean', 'Dragon rider war college', NOW(), NOW());

INSERT INTO books (title, isbn, release_date, description, image_url, average_rating, rating_count, series_id,
                   created_date, edited_date)
VALUES ('A Court of Thorns and Roses', '9781619634442', '2015-05-05', 'Fae fantasy romance', NULL, 4.6, 1200,
        (SELECT id FROM book_series WHERE title = 'A Court of Thorns and Roses'), NOW(), NOW()),
       ('A Court of Mist and Fury', '9781619634466', '2016-05-03', 'Sequel in the ACOTAR series', NULL, 4.7, 980,
        (SELECT id FROM book_series WHERE title = 'A Court of Thorns and Roses'), NOW(), NOW()),
       ('The Hunger Games', '9780439023528', '2008-09-14', 'Teens fight to survive in dystopia', NULL, 4.5, 2100,
        (SELECT id FROM book_series WHERE title = 'The Hunger Games'), NOW(), NOW()),
       ('Catching Fire', '9780439023498', '2009-09-01', 'Second Hunger Games book', NULL, 4.4, 1850,
        (SELECT id FROM book_series WHERE title = 'The Hunger Games'), NOW(), NOW()),
       ('The Love Hypothesis', '9780593336823', '2021-09-14', 'STEM fake dating romance', NULL, 4.2, 760,
        NULL, NOW(), NOW()),
       ('Fourth Wing', '9781649374042', '2023-04-05', 'Dragon riding war college', NULL, 4.6, 1500,
        (SELECT id FROM book_series WHERE title = 'Empyrean'), NOW(), NOW());

INSERT INTO book_authors (book_id, author_id)
VALUES ((SELECT id FROM books WHERE isbn = '9781619634442'),
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
        (SELECT id FROM authors WHERE full_name = 'Rebecca Yarros'));

INSERT INTO book_genres (book_id, genre_id)
VALUES ((SELECT id FROM books WHERE isbn = '9781619634442'), (SELECT id FROM genres WHERE name = 'fantasy')),
       ((SELECT id FROM books WHERE isbn = '9781619634442'), (SELECT id FROM genres WHERE name = 'romance')),
       ((SELECT id FROM books WHERE isbn = '9781619634466'), (SELECT id FROM genres WHERE name = 'fantasy')),
       ((SELECT id FROM books WHERE isbn = '9781619634466'), (SELECT id FROM genres WHERE name = 'romance')),
       ((SELECT id FROM books WHERE isbn = '9780439023528'), (SELECT id FROM genres WHERE name = 'dystopian')),
       ((SELECT id FROM books WHERE isbn = '9780439023498'), (SELECT id FROM genres WHERE name = 'dystopian')),
       ((SELECT id FROM books WHERE isbn = '9780593336823'), (SELECT id FROM genres WHERE name = 'romance')),
       ((SELECT id FROM books WHERE isbn = '9781649374042'), (SELECT id FROM genres WHERE name = 'fantasy'));

INSERT INTO profiles (image_url, deleted, created_date, edited_date)
VALUES (NULL, false, NOW(), NOW());

INSERT INTO profile_favorite_books (profile_id, book_id)
VALUES ((SELECT id FROM profiles ORDER BY created_date LIMIT 1),
       (SELECT id FROM books WHERE isbn = '9781619634442')),
       ((SELECT id FROM profiles ORDER BY created_date LIMIT 1), (SELECT id FROM books WHERE isbn = '9780593336823')),
       ((SELECT id FROM profiles ORDER BY created_date LIMIT 1), (SELECT id FROM books WHERE isbn = '9781649374042'));

INSERT INTO profile_favorite_authors (profile_id, author_id)
VALUES ((SELECT id FROM profiles ORDER BY created_date LIMIT 1),
       (SELECT id FROM authors WHERE full_name = 'Sarah J. Maas')),
       ((SELECT id FROM profiles ORDER BY created_date LIMIT 1), (SELECT id FROM authors WHERE full_name = 'Rebecca Yarros'));

INSERT INTO ratings (score, review, book_id, profile_id, created_date, edited_date)
VALUES (5, 'Obsessed.',
        (SELECT id FROM books WHERE isbn = '9781619634442'),
        (SELECT id FROM profiles ORDER BY created_date LIMIT 1), NOW(), NOW()),
       (4, 'Loved the tension.',
        (SELECT id FROM books WHERE isbn = '9781649374042'),
        (SELECT id FROM profiles ORDER BY created_date LIMIT 1),
        NOW(), NOW());

INSERT INTO lists (name, description, profile_id, created_date, edited_date)
VALUES ('Fantasy Faves', 'My top fantasy books',
        (SELECT id FROM profiles ORDER BY created_date LIMIT 1), NOW(), NOW()),
       ('Want To Read', 'Books I plan to read soon',
        (SELECT id FROM profiles ORDER BY created_date LIMIT 1), NOW(), NOW());

INSERT INTO list_books (list_id, book_id)
VALUES ((SELECT id FROM lists WHERE name = 'Fantasy Faves'), (SELECT id FROM books WHERE isbn = '9781619634442')),
       ((SELECT id FROM lists WHERE name = 'Fantasy Faves'), (SELECT id FROM books WHERE isbn = '9781649374042')),
       ((SELECT id FROM lists WHERE name = 'Want To Read'), (SELECT id FROM books WHERE isbn = '9781619634466')),
       ((SELECT id FROM lists WHERE name = 'Want To Read'), (SELECT id FROM books WHERE isbn = '9780593336823'));