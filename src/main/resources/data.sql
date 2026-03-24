INSERT INTO genres (id, name, description, created_date, edited_date)
VALUES (1, 'fantasy', 'Magic, worlds, mythical creatures', NOW(), NOW()),
       (2, 'romance', 'Love stories and relationships', NOW(), NOW()),
       (3, 'dystopian', 'Broken societies and survival', NOW(), NOW()),
       (4, 'science fiction', 'Futuristic science themes', NOW(), NOW());

INSERT INTO authors (id, full_name, created_date, edited_date)
VALUES (1, 'Sarah J. Maas', NOW(), NOW()),
       (2, 'Suzanne Collins', NOW(), NOW()),
       (3, 'Ali Hazelwood', NOW(), NOW()),
       (4, 'Rebecca Yarros', NOW(), NOW());

INSERT INTO book_series (id, title, description, created_date, edited_date)
VALUES (1, 'A Court of Thorns and Roses', 'Fae romance saga', NOW(), NOW()),
       (2, 'The Hunger Games', 'Dystopian survival trilogy', NOW(), NOW()),
       (3, 'Empyrean', 'Dragon rider war college', NOW(), NOW());

INSERT INTO books (id, title, isbn, release_date, description, image_url, average_rating, rating_count, series_id,
                   created_date, edited_date)
VALUES (1, 'A Court of Thorns and Roses', '9781619634442', '2015-05-05', 'Fae fantasy romance', NULL, 4.6, 1200, 1,
        NOW(), NOW()),
       (2, 'A Court of Mist and Fury', '9781619634466', '2016-05-03', 'Sequel in the ACOTAR series', NULL, 4.7, 980, 1,
        NOW(), NOW()),
       (3, 'The Hunger Games', '9780439023528', '2008-09-14', 'Teens fight to survive in dystopia', NULL, 4.5, 2100, 2,
        NOW(), NOW()),
       (4, 'Catching Fire', '9780439023498', '2009-09-01', 'Second Hunger Games book', NULL, 4.4, 1850, 2, NOW(),
        NOW()),
       (5, 'The Love Hypothesis', '9780593336823', '2021-09-14', 'STEM fake dating romance', NULL, 4.2, 760, NULL,
        NOW(), NOW()),
       (6, 'Fourth Wing', '9781649374042', '2023-04-05', 'Dragon riding war college', NULL, 4.6, 1500, 3, NOW(), NOW());

INSERT INTO book_authors (book_id, author_id)
VALUES (1, 1),
       (2, 1),
       (3, 2),
       (4, 2),
       (5, 3),
       (6, 4);

INSERT INTO book_genres (book_id, genre_id)
VALUES (1, 1),
       (1, 2),
       (2, 1),
       (2, 2),
       (3, 3),
       (4, 3),
       (5, 2),
       (6, 1);

INSERT INTO profiles (id, image_url, deleted, created_date, edited_date)
VALUES (1, NULL, false, NOW(), NOW());

INSERT INTO profile_favorite_books (profile_id, book_id)
VALUES (1, 1),
       (1, 5),
       (1, 6);

INSERT INTO profile_favorite_authors (profile_id, author_id)
VALUES (1, 1),
       (1, 4);

INSERT INTO ratings (id, score, review, book_id, profile_id, created_date, edited_date)
VALUES (1, 5, 'Obsessed.', 1, 1, NOW(), NOW()),
       (2, 4, 'Loved the tension.', 6, 1, NOW(), NOW());

-- ======================
-- LISTS
-- ======================
INSERT INTO lists (id, name, description, profile_id, created_date, edited_date)
VALUES (1, 'Fantasy Faves', 'My top fantasy books', 1, NOW(), NOW()),
       (2, 'Want To Read', 'Books I plan to read soon', 1, NOW(), NOW());

-- ======================
-- LIST <-> BOOKS
-- ======================
INSERT INTO list_books (list_id, book_id)
VALUES (1, 1),   -- ACOTAR
       (1, 6),   -- Fourth Wing
       (2, 2),   -- ACOMAF
       (2, 5);   -- The Love Hypothesis


