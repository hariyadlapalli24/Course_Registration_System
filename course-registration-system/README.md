# ABC University — Course Registration System

A full-stack course registration app:

- **Frontend:** React (React Router, Axios) + plain CSS
- **Backend:** Java 17 + Spring Boot (Spring Web, Spring Data JPA, Spring Security)
- **Database:** MySQL

```
course-registration-system/
├── backend/       Spring Boot REST API
├── frontend/       React app
└── database/       Reference SQL schema (auto-created by Hibernate at runtime)
```

## 1. Prerequisites

- Java 17+ and Maven (or use an IDE like IntelliJ that bundles both)
- Node.js 18+ and npm
- MySQL 8.x running locally

## 2. Database setup

You don't need to create tables by hand — Hibernate creates/updates them
automatically on startup. You only need the MySQL server itself running,
with a user that can create databases.

```sql
-- If your MySQL user doesn't already have permission to create databases:
CREATE USER IF NOT EXISTS 'root'@'localhost' IDENTIFIED BY 'your_mysql_password';
GRANT ALL PRIVILEGES ON *.* TO 'root'@'localhost';
```

`database/schema.sql` is included as a reference if you'd rather manage
the schema by hand (see the comment at the top of that file for how).

## 3. Backend setup

```bash
cd backend
```

Edit `src/main/resources/application.properties` and set your real MySQL
username/password:

```properties
spring.datasource.username=root
spring.datasource.password=your_mysql_password
```

Then run it:

```bash
./mvnw spring-boot:run
```

(or `mvn spring-boot:run` if you have Maven installed globally, or just run
`RegistrationApplication.java` from your IDE).

The API starts on **http://localhost:8080**. On first boot,
`config/DataSeeder.java` automatically inserts:

- A sample course catalog (CS101, CS204, CS310, CS415, MA201, EN110)
- One bootstrap admin account:
  - Email: `admin@abcuniversity.edu`
  - Password: `ChangeMe123!`

**Change that admin password (or create a new admin and disable this one)
before using this anywhere but your own machine.**

## 4. Frontend setup

```bash
cd frontend
npm install
npm start
```

The app opens on **http://localhost:3000** and talks to the backend at
`http://localhost:8080/api` by default. To point it elsewhere, create a
`.env` file in `frontend/`:

```
REACT_APP_API_BASE_URL=http://localhost:8080/api
```

## 5. Using the app

1. Go to `http://localhost:3000` → click **Register** → create a student account.
2. Click **Login** with that email/password → you land on the **Dashboard**,
   showing your name, department, and roll number.
3. Click **Enroll for course registration** → check the courses you want →
   **Register** → see "Registration successful."
4. To see the admin side, log in with the seeded admin account above — you're
   redirected to `/admin` instead, showing every student and every enrollment.

## How the API is organized

| Method | Endpoint                 | Access          | Purpose                              |
|--------|---------------------------|-----------------|---------------------------------------|
| POST   | `/api/users/register`     | Public          | Create a student (role always `USER`) |
| POST   | `/api/users/login`        | Public          | Validate credentials, issue a token   |
| GET    | `/api/users/{id}`         | Self or admin   | Fetch a profile                       |
| GET    | `/api/courses`            | Public          | List the course catalog               |
| POST   | `/api/enrollments`        | Self or admin   | Enroll in one or more courses         |
| GET    | `/api/admin/users`        | Admin only      | List every student                    |
| GET    | `/api/admin/enrollments`  | Admin only      | List every enrollment                 |
| POST   | `/api/admin/admins`       | Admin only      | Create another admin account          |

## How roles and security actually work

This uses a deliberately simple but *real* server-enforced auth model,
built so it's easy to later swap in full JWT/OAuth without touching the
rest of the app:

- **Passwords** are hashed with BCrypt (`SecurityConfig.passwordEncoder()`),
  never stored or returned in plain text.
- **Login** issues a random opaque token (`TokenStore.issueToken`), kept in
  an in-memory map on the server alongside the user's id, email, and role.
  The frontend stores this token in `localStorage` and sends it as
  `Authorization: Bearer <token>` on every request.
- **`TokenAuthenticationFilter`** reads that header on each request, looks
  the token up server-side, and tells Spring Security what role the caller
  actually has — this role comes only from what was stored at login time,
  never from anything in the request body.
- **`SecurityConfig`** declares `/api/admin/**` as `hasRole("ADMIN")`. If a
  USER token (even a deliberately tampered client) calls an admin endpoint,
  Spring Security itself returns `403 Forbidden` before the controller code
  ever runs.
- **Registration is role-locked:** `RegisterRequest` has no `role` field at
  all, and `UserService.register()` always sets `Role.USER`. There is no
  public UI or endpoint where a client can request `ADMIN`.
- **The only way an admin account is created** is (a) the bootstrap admin
  seeded by `DataSeeder` on first run, or (b) an existing admin calling
  `POST /api/admin/admins` — itself behind the same `hasRole("ADMIN")` rule.

Note: the token store is in-memory, so tokens reset if you restart the
backend, and it won't work across multiple backend instances without a
shared store (e.g. Redis) — perfectly fine for local development or a
single-instance deployment, but worth knowing if you later scale this out.

## Extending this later

- Swap `TokenStore`/`TokenAuthenticationFilter` for JWTs or Spring Session —
  `SecurityConfig`'s `authorizeHttpRequests` rules don't need to change.
- Add password-reset, email verification, or refresh tokens in `UserService`.
- Add course capacity limits or prerequisites in `EnrollmentService`.
