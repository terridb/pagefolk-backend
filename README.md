[![CI](https://github.com/terridb/pagefolk-backend/actions/workflows/ci.yml/badge.svg)](https://github.com/terridb/pagefolk-backend/actions/workflows/ci.yml)
[![CD](https://github.com/terridb/pagefolk-backend/actions/workflows/cd.yml/badge.svg)](https://github.com/terridb/pagefolk-backend/actions/workflows/cd.yml)

# Pagefolk-backend

A book management REST API built with Spring Boot 4 and Java 25. Supports browsing books, authors, genres, and series; user ratings and reading lists; and role-based access control via Keycloak.

## Tech stack

- **Spring Boot 4** (Web MVC, Data JPA, Security, Actuator)
- **Java 25**
- **PostgreSQL** - primary database
- **Keycloak** - OAuth 2.0 / JWT authentication and authorization
- **Lombok**

## Prerequisites

- Java 25
- Maven (or use the included `./mvnw` wrapper)
- PostgreSQL instance
- Keycloak instance with a `Pagefolk` realm configured

## Local development

### 1. Configure environment variables

```bash
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=pagefolk
export DB_USER=your_user
export DB_PASSWORD=your_password

export CLIENT_ID=your_keycloak_client_id
export CLIENT_SECRET=your_keycloak_client_secret
export ISSUER_URI=http://localhost:9090/realms/Pagefolk
export AUDIENCE=your_audience
```

### 2. Run

```bash
./mvnw spring-boot:run
```

The app starts on port **8080**. The schema is created automatically on startup (`create-drop` by default) and seed data is loaded from `src/main/resources/data.sql`.

To use a persistent schema instead, set `DDL_AUTO=update`.

### 3. Run tests

```bash
# All tests (unit + integration)
./mvnw verify

# Unit tests only
./mvnw test

# Single test class
./mvnw test -Dtest=PagefolkApplicationTests

# Single test method
./mvnw test -Dtest=PagefolkApplicationTests#contextLoads
```

## Docker Compose

The included `docker-compose.yml` spins up the API, PostgreSQL, and Keycloak together. Keycloak is pre-configured via `keycloak/realm-export.json`.

Create a `.env` file in the project root:

```env
POSTGRES_USER=pagefolk
POSTGRES_PASSWORD=secret
POSTGRES_DB=pagefolk

CLIENT_ID=pagefolk-client
CLIENT_SECRET=your_client_secret
AUDIENCE=pagefolk-client

KEYCLOAK_ADMIN=admin
KEYCLOAK_ADMIN_PASSWORD=admin
```

Then start everything:

```bash
docker compose up --build
```

| Service  | Port |
|----------|------|
| API      | 8080 |
| Keycloak | 9090 |

## API overview

All write operations require a valid JWT bearer token. Roles used for authorization: `ADMIN`, `MODERATOR`.

### Books — `/books`

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| GET | `/books` | public | List books (filterable, paginated) |
| GET | `/books/{id}` | public | Get book by ID |
| POST | `/books` | ADMIN | Create a book |
| PATCH | `/books/{id}` | ADMIN | Update a book |
| PATCH | `/books/{id}/delete` | ADMIN | Soft-delete a book |
| PATCH | `/books/{id}/revive` | ADMIN | Restore a soft-deleted book |
| DELETE | `/books/{id}` | ADMIN | Hard-delete (requires prior soft-delete) |

Filter params: `search`, `genres`, `authorIds`, `seriesId`, `minRating`, `releaseAfter`, `releaseBefore`, `page`, `size`, `sortBy`, `direction`

### Authors — `/authors`

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| GET | `/authors` | public | List authors (filterable, paginated) |
| GET | `/authors/{id}` | public | Get author by ID |
| POST | `/authors` | ADMIN | Create an author |
| PUT | `/authors/{id}` | ADMIN | Update an author |
| PATCH | `/authors/{id}/delete` | ADMIN | Soft-delete an author |
| PATCH | `/authors/{id}/revive` | ADMIN | Restore a soft-deleted author |
| DELETE | `/authors/{id}` | ADMIN | Hard-delete (requires prior soft-delete) |

### Genres — `/genres`

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| GET | `/genres` | public | List all genres |
| GET | `/genres/{id}` | public | Get genre by ID |
| POST | `/genres` | ADMIN | Create a genre |
| PATCH | `/genres/{id}` | ADMIN | Update a genre |
| DELETE | `/genres/{id}` | ADMIN | Delete a genre |

### Series — `/series`

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| GET | `/series` | public | List series (paginated) |
| GET | `/series/{id}` | public | Get series by ID |
| POST | `/series` | ADMIN | Create a series |
| PATCH | `/series/{id}` | ADMIN | Update a series |
| DELETE | `/series/{id}` | ADMIN | Delete a series |

### Ratings — `/ratings`

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| GET | `/ratings` | ADMIN, MODERATOR | List all ratings |
| GET | `/ratings/{id}` | ADMIN, MODERATOR | Get rating by ID |
| GET | `/ratings/user/{userId}` | public | Ratings by user |
| GET | `/ratings/book/{bookId}` | public | Ratings for a book |
| POST | `/ratings` | authenticated | Submit a rating |
| PUT | `/ratings/{id}` | authenticated | Update a rating |
| DELETE | `/ratings/{id}` | authenticated | Delete a rating |

### Lists — `/lists`

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| GET | `/lists/users/{userId}` | public | Lists belonging to a user |
| GET | `/lists/{id}` | ADMIN, MODERATOR | Get list by ID |
| POST | `/lists` | ADMIN, MODERATOR | Create a list |
| PATCH | `/lists/{id}` | ADMIN, MODERATOR | Update a list |
| POST | `/lists/{listId}/books/{bookId}` | ADMIN, MODERATOR | Add book to list |
| DELETE | `/lists/{listId}/books/{bookId}` | ADMIN, MODERATOR | Remove book from list |
| DELETE | `/lists/{id}` | ADMIN, MODERATOR | Delete a list |

### Users — `/users`

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| GET | `/users` | public | List users |
| GET | `/users/{id}` | public | Get user profile |
| GET | `/users/me` | authenticated | Get own profile (creates user on first call) |
| PATCH | `/users/me` | authenticated | Update own profile |
| PATCH | `/users/me/delete` | authenticated | Soft-delete own account |
| GET | `/users/admin/all` | ADMIN, MODERATOR | List all users (admin view) |
| GET | `/users/admin/{id}` | ADMIN, MODERATOR | Get user (admin view) |
| PATCH | `/users/admin/{id}/delete` | ADMIN, MODERATOR | Soft-delete a user |
| PATCH | `/users/admin/{id}/revive` | ADMIN, MODERATOR | Restore a user |
| DELETE | `/users/admin/{id}` | ADMIN | Hard-delete a user |

### Health — `/actuator/health`

Public endpoint. Returns application health status.

## Error responses

| Status | Trigger |
|--------|---------|
| 400 | Invalid request body or query params |
| 404 | Resource not found |
| 409 | Conflict (e.g. duplicate) |
