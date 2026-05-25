# User Task API

Spring Boot REST API for managing users and their tasks, with **Spring Security** (authentication, roles, BCrypt passwords).

## Tech stack

- Java 21
- Spring Boot 3
- Spring Web
- Spring Data JPA (Hibernate)
- **Spring Security** (form login, HTTP Basic, BCrypt, method security)
- H2 Database
- Bean Validation
- SpringDoc OpenAPI (Swagger)
- Lombok

## Project structure

Layered architecture:

- `controller` — REST endpoints
- `service` — business logic
- `repository` — data access
- `entity` — JPA entities
- `dto` — request/response models
- `exception` — global error handling
- `config` — Security, OpenAPI, seed data
- `security` — UserDetails, REST 401/403 handlers

## Data model

- `User` (id, name, email, **password**, **role**)
- `Task` (id, title, description, status, user)
- Relationship: one user has many tasks (`OneToMany` / `ManyToOne`)
- Roles: `USER`, `ADMIN`

## How to run

1. Open the project in IntelliJ or Cursor.
2. Run `UserTaskApiApplication`.
3. Application starts on **http://localhost:8081**.

## Useful URLs

- Swagger UI: http://localhost:8081/swagger-ui.html
- H2 Console: http://localhost:8081/h2-console

H2 login values:

- JDBC URL: `jdbc:h2:mem:usertaskdb`
- Username: `sa`
- Password: *(empty)*

---

## Security

### Login credentials (seeded on startup)

| Email | Password | Role |
|-------|----------|------|
| `admin@example.com` | `admin123` | ADMIN |
| `user@example.com` | `user123` | USER |

Additional accounts can be created via `POST /api/auth/register` (always assigned `USER` role).

Passwords are stored using **BCrypt** (`BCryptPasswordEncoder`).

### Login and logout

**Option A — JSON login in Swagger (recommended, no browser popup)**

1. Open **Authentication** → `POST /api/auth/login` → **Try it out**.
2. Request body example:

```json
{
  "email": "user@example.com",
  "password": "user123"
}
```

3. Execute — you should get `200` and `"Login successful"`.
4. Then call `GET /api/auth/me` or any task endpoint (same browser tab keeps the session cookie).

**Option B — HTTP Basic in Swagger**

1. Click **Authorize** (lock icon).
2. **Username** must be the full **email** (`user@example.com`), not the display name.
3. **Password** = account password (`user123`).
4. Click **Authorize**, then **Close**.
5. If the **browser** shows its own username/password popup, click **Cancel** — use only Swagger’s Authorize dialog, not the browser popup.

**Logout**

```http
POST /api/auth/logout
```

(Use the same browser tab so the session cookie is sent.)

### User roles

| Role | Description |
|------|-------------|
| `USER` | Can use task APIs and view/update user profiles by id |
| `ADMIN` | Full user management: list all users, create users with any role, delete users |

### Endpoint access rules

| Endpoint | Access |
|----------|--------|
| `POST /api/auth/register` | **Public** |
| `POST /api/auth/login` | **Public** |
| `POST /api/auth/logout` | Authenticated |
| `GET /api/auth/me` | **Authenticated** (any role) |
| `/api/tasks/**` | **Authenticated** (USER or ADMIN) |
| `GET /api/users/{id}`, `PUT /api/users/{id}` | **Authenticated** |
| `GET /api/users` | **ADMIN only** |
| `POST /api/users` | **ADMIN only** |
| `DELETE /api/users/{id}` | **ADMIN only** |
| Swagger UI, H2 console, OpenAPI docs | **Public** |

### ADMIN-only functionality

- List all users (`GET /api/users`)
- Create users with a chosen role (`POST /api/users`)
- Delete users (`DELETE /api/users/{id}`)
- Enforced at **URL level** (`SecurityConfig`) and **method level** (`@PreAuthorize("hasRole('ADMIN')")` on `UserServiceImpl`)

### Authenticated-only functionality

- All task CRUD under `/api/tasks/**`
- Current user profile: `GET /api/auth/me`
- Enforced at URL level and with `@PreAuthorize("isAuthenticated()")` on task service methods and `AuthServiceImpl.getCurrentUser()`

### Method security (`@PreAuthorize`)

Enabled via `@EnableMethodSecurity` in `SecurityConfig`. Examples:

- `@PreAuthorize("hasRole('ADMIN')")` — `UserServiceImpl.create`, `findAll`, `delete`
- `@PreAuthorize("isAuthenticated()")` — `TaskServiceImpl` methods, `AuthServiceImpl.getCurrentUser()`

### CSRF

**CSRF is disabled** in `SecurityConfig` because this project is a **REST API** tested with Swagger UI and HTTP clients. Spring Security’s CSRF tokens are designed for browser form posts; stateless/JSON APIs typically use tokens (JWT) or session + Basic auth instead. Re-enable CSRF if you add server-rendered HTML forms.

---

## Main endpoints

### Authentication

- `POST /api/auth/register` — public registration
- `POST /api/auth/login` — form login
- `POST /api/auth/logout` — logout
- `GET /api/auth/me` — current user profile

### Users

- `POST /api/users` — ADMIN only
- `GET /api/users` — ADMIN only
- `GET /api/users/{id}` — authenticated
- `PUT /api/users/{id}` — authenticated
- `DELETE /api/users/{id}` — ADMIN only

### Tasks

- `POST /api/tasks` — authenticated
- `GET /api/tasks` — authenticated
- `GET /api/tasks/{id}` — authenticated
- `PUT /api/tasks/{id}` — authenticated
- `DELETE /api/tasks/{id}` — authenticated

## Notes

- Validation is applied on request DTOs.
- API returns proper error responses (400, 401, 403, 404, 409, 500).
- Entities are never returned directly (DTOs only).
- Passwords are never returned in API responses.

## API test screenshots

### User endpoints

Invalid user create request (validation error):
![img_1.png](screenshots/img_1.png)

Valid user create request:
![img_2.png](screenshots/img_2.png)

Get user by ID:
![img_3.png](screenshots/img_3.png)

Get all users:
![img_5.png](screenshots/img_5.png)

Update user:
![img_4.png](screenshots/img_4.png)

Delete user:
![img_6.png](screenshots/img_6.png)

### Task endpoints

Create task:
![img_7.png](screenshots/img_7.png)

Get all tasks:
![img_8.png](screenshots/img_8.png)

Update task:
![img_9.png](screenshots/img_9.png)

Get task by ID:
![img_10.png](screenshots/img_10.png)

Delete task:
![img_11.png](screenshots/img_11.png)

### Database (H2)

Users table in H2 console:
![img_12.png](screenshots/img_12.png)

Tasks table in H2 console:
![img_13.png](screenshots/img_13.png)
