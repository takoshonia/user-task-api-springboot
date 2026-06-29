# User Task API

Spring Boot REST API for managing users and their tasks. Uses Spring Security with roles and BCrypt passwords.

## Tech stack

- Java 21
- Spring Boot 3
- Spring Web
- Spring Data JPA (Hibernate)
- Spring Security (HTTP Basic + JSON login, BCrypt, method security)
- H2 Database (dev) / PostgreSQL (prod)
- Bean Validation
- SpringDoc OpenAPI (Swagger)
- Spring Boot Actuator (health, info, metrics)
- JUnit 5, Mockito, MockMvc, JaCoCo

## Project structure

Layered architecture:

- `controller` - REST endpoints
- `service` - business logic
- `repository` - data access
- `entity` - JPA entities
- `dto` - request/response models
- `exception` - global error handling
- `config` - Security, OpenAPI, AppSettings, Actuator, locale, seed data
- `security` - UserDetails, REST 401/403 handlers



## Data model

- `User` (id, name, email, password, role)
- `Task` (id, title, description, status, user)
- One user has many tasks (`OneToMany` / `ManyToOne`)
- Roles: `USER`, `ADMIN`



## How to run

**IDE:** Run `UserTaskApiApplication` (active profile `dev` by default).

**Terminal (dev):**

```powershell
mvn spring-boot:run
```

**Terminal (prod):** needs local PostgreSQL (`usertaskdb`). Set credentials in `application-prod.properties`.

```powershell
mvn spring-boot:run "-Dspring-boot.run.profiles=prod"
```

Dev uses H2 and seeds test users. Prod uses PostgreSQL. App runs on [http://localhost:8081](http://localhost:8081).

### Quick links (dev)

| Tool | URL | Notes |
| ---- | --- | ----- |
| Swagger UI | [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html) | Test all API endpoints |
| H2 Console | [http://localhost:8081/h2-console](http://localhost:8081/h2-console) | DB browser (dev only); see [H2 login](#useful-urls) below |
| Actuator health | [http://localhost:8081/actuator/health](http://localhost:8081/actuator/health) | Public |
| Actuator info | [http://localhost:8081/actuator/info](http://localhost:8081/actuator/info) | Public |
| Actuator metrics | [http://localhost:8081/actuator/metrics](http://localhost:8081/actuator/metrics) | ADMIN only |

### Profiles


| Profile         | Database     | Logging      | Notes                                                          |
| --------------- | ------------ | ------------ | -------------------------------------------------------------- |
| `dev` (default) | H2 in-memory | DEBUG / INFO | Seeds admin and user accounts on startup                       |
| `prod`          | PostgreSQL   | WARN / ERROR | No seed data; set DB password in `application-prod.properties` |


**IDE:** Run `UserTaskApiApplication` → Run Configuration → Active profiles: `dev` or `prod`.

**Command line:**

```powershell
# dev (default)
mvn spring-boot:run

# prod
mvn spring-boot:run "-Dspring-boot.run.profiles=prod"
```

Config files: `application.properties` (shared), `application-dev.properties`, `application-prod.properties`.

### Custom properties (`app.settings`)

Defined in `AppSettings` (`@ConfigurationProperties`, `@Validated`):


| Property                         | Role                               |
| -------------------------------- | ---------------------------------- |
| `app.settings.application-title` | API title shown in `GET /api/info` |
| `app.settings.pagination-limit`  | Page size limit (metadata)         |
| `app.settings.contact-email`     | Contact email (metadata)           |


Dev example: title `User Task API (Dev)`, limit `10`, email `dev@example.com`.  
Prod example: title `User Task API`, limit `50`, email `support@example.com`.

### Internationalization (i18n)

Bundles: `messages.properties` (Georgian default), `messages_en.properties` (English). UTF-8 encoding via `spring.messages.encoding=UTF-8`.

Locale is resolved from the `Accept-Language` header (`AcceptHeaderLocaleResolver`). Supported: `ka` (default), `en`.

Localized responses:

- Error payloads from `GlobalExceptionHandler` (404, 409, 403, 401, 400, 500)
- Security errors from `RestAuthenticationEntryPoint` and `RestAccessDeniedHandler`
- Success messages on `POST /api/auth/login` and `POST /api/auth/logout`
- Validation field errors on `POST /api/auth/register` (`name`, `email` use bundle keys)

**How to test:** In Swagger, set **Accept-Language** to `en` or `ka`, then:

1. `GET /api/tasks` without auth → 401 message changes by language
2. `POST /api/auth/register` with empty body → field errors change by language
3. `POST /api/auth/login` with valid credentials → success message changes by language



### Logging

SLF4J / Logback in `AuthController`, `AuthServiceImpl`, `UserServiceImpl`, `TaskServiceImpl`, and `GlobalExceptionHandler`.


| Level | Where                                                               |
| ----- | ------------------------------------------------------------------- |
| INFO  | Registration, login/logout, task creation, admin user create/delete |
| DEBUG | Task deletion (dev profile)                                         |
| WARN  | Validation, access denied, not found, bad credentials               |
| ERROR | Unexpected exceptions                                               |


- Console and file logging (`logs/app.log`)
- Rolling policy: 10 MB per file, 7 days history
- Profile levels: dev `DEBUG`/`INFO`, prod `WARN`/`ERROR`
- Parameterized messages (e.g. `log.info("User registered: {}", email)`)



### Testing

Run all tests:

```powershell
mvn test
```

Generate JaCoCo coverage report:

```powershell
mvn test jacoco:report
```

Report: `target/site/jacoco/index.html`


| Type                                                           | Examples                                                                           |
| -------------------------------------------------------------- | ---------------------------------------------------------------------------------- |
| Unit (`@ExtendWith(MockitoExtension.class)`)                   | `AuthServiceImplTest`, `UserServiceImplTest`, `TaskServiceImplTest`                |
| `@WebMvcTest`                                                  | `InfoControllerTest`                                                               |
| `@DataJpaTest`                                                 | `UserRepositoryTest`, `TaskRepositoryTest`                                         |
| Integration (`@SpringBootTest` + MockMvc / `TestRestTemplate`) | `ApiSecurityIntegrationTest`, `TaskFlowIntegrationTest`, `ActuatorIntegrationTest` |


Covers positive and negative cases: successful registration, duplicate email, 401/403 security, validation errors (parameterized), task ownership, actuator access rules.

### Monitoring (Actuator)


| Endpoint                | Access     | Description                      |
| ----------------------- | ---------- | -------------------------------- |
| `GET /actuator/health`  | Public     | Application health (`UP`/`DOWN`) |
| `GET /actuator/info`    | Public     | App metadata from `AppSettings`  |
| `GET /actuator/metrics` | ADMIN only | Micrometer metrics list          |


Examples:

```powershell
# health (no auth)
curl http://localhost:8081/actuator/health

# info (no auth)
curl http://localhost:8081/actuator/info

# metrics (admin basic auth)
curl -u admin@example.com:admin123 http://localhost:8081/actuator/metrics
```

Health details are shown only for authenticated ADMIN users (`show-details=when-authorized`).

---



## Useful URLs

- Swagger UI: [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html)
- H2 Console: [http://localhost:8081/h2-console](http://localhost:8081/h2-console)

H2 login values:

- JDBC URL: `jdbc:h2:mem:usertaskdb`
- Username: `sa`
- Password: *(empty)*

---



## Security



### Login credentials (seeded on startup)


| Email               | Password   | Role  |
| ------------------- | ---------- | ----- |
| `admin@example.com` | `admin123` | ADMIN |
| `user@example.com`  | `user123`  | USER  |


New accounts can be created via `POST /api/auth/register`. They always get the `USER` role.

Passwords are stored using BCrypt (`BCryptPasswordEncoder`).

### Login and logout

**Option A: JSON login in Swagger**

1. Open `POST /api/auth/login`, click Try it out.
2. Request body:

```json
{
  "email": "user@example.com",
  "password": "user123"
}
```

1. Execute. You should get 200 and a localized success message (`Accept-Language` applies).
2. Then call `GET /api/auth/me` or any task endpoint in the same tab.

**Option B: HTTP Basic in Swagger**

1. Click Authorize (lock icon).
2. Username is the full email (`user@example.com`).
3. Password is the account password (`user123`).
4. Click Authorize, then Close.
5. If the browser shows its own popup, click Cancel and use only Swagger's Authorize dialog.

**Logout**

```http
POST /api/auth/logout
```

Use the same browser tab so the session cookie is sent.

### User roles


| Role    | Description                                       |
| ------- | ------------------------------------------------- |
| `USER`  | Can manage own tasks and own profile only         |
| `ADMIN` | Can manage all users, all profiles, and all tasks |




### Endpoint access rules


| Endpoint                                     | Access                                      |
| -------------------------------------------- | ------------------------------------------- |
| `GET /api/info`                              | Public                                      |
| `GET /actuator/health`, `GET /actuator/info` | Public                                      |
| `GET /actuator/metrics`                      | ADMIN only                                  |
| `POST /api/auth/register`                    | Public                                      |
| `POST /api/auth/login`                       | Public                                      |
| `POST /api/auth/logout`                      | Authenticated                               |
| `GET /api/auth/me`                           | Authenticated                               |
| `POST /api/tasks`                            | Authenticated (owner = current user)        |
| `GET /api/tasks`                             | Authenticated (USER: own tasks; ADMIN: all) |
| `GET/PUT/DELETE /api/tasks/{id}`             | Task owner or ADMIN                         |
| `GET /api/users/{id}`, `PUT /api/users/{id}` | Self or ADMIN                               |
| `GET /api/users`                             | ADMIN only                                  |
| `POST /api/users`                            | ADMIN only                                  |
| `DELETE /api/users/{id}`                     | ADMIN only                                  |
| Swagger UI, H2 console, OpenAPI docs         | Public                                      |




### ADMIN-only functionality

- List all users (`GET /api/users`)
- Create users with a chosen role (`POST /api/users`)
- Delete users (`DELETE /api/users/{id}`)
- Checked at URL level in `SecurityConfig` and at method level with `@PreAuthorize("hasRole('ADMIN')")` on `UserServiceImpl`.



### Authenticated-only functionality

- All task CRUD under `/api/tasks/**`
- Current user profile: `GET /api/auth/me`
- Checked at URL level and with `@PreAuthorize("isAuthenticated()")` on task service methods and `AuthServiceImpl.getCurrentUser()`.



### Method security (`@PreAuthorize`)

Enabled via `@EnableMethodSecurity` in `SecurityConfig`. Examples:

- `@PreAuthorize("hasRole('ADMIN')")` on `UserServiceImpl.create`, `findAll`, `delete`
- `@PreAuthorize("isAuthenticated()")` on `TaskServiceImpl` methods and `AuthServiceImpl.getCurrentUser()`



### CSRF

Disabled in `SecurityConfig` (REST API, no HTML forms).

---



## Main endpoints



### Authentication

- `GET /api/info` - public app settings
- `POST /api/auth/register` - public registration
- `POST /api/auth/login` - JSON login
- `POST /api/auth/logout` - logout
- `GET /api/auth/me` - current user profile



### Users

- `POST /api/users` - ADMIN only
- `GET /api/users` - ADMIN only
- `GET /api/users/{id}` - self or ADMIN
- `PUT /api/users/{id}` - self or ADMIN (partial updates supported)
- `DELETE /api/users/{id}` - ADMIN only



### Tasks

- `POST /api/tasks` - authenticated, owner set to current user
- `GET /api/tasks` - USER sees own tasks, ADMIN sees all
- `GET /api/tasks/{id}` - task owner or ADMIN
- `PUT /api/tasks/{id}` - task owner or ADMIN
- `DELETE /api/tasks/{id}` - task owner or ADMIN



## Notes

- Validation is applied on request DTOs.
- API returns proper error responses (400, 401, 403, 404, 409, 500).
- Entities are never returned directly (only DTOs).
- Passwords are never returned in API responses.

