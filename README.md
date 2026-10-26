# Sai Kumar Dash portfolio (Spring Boot + SQL)

Spring Boot serves the portfolio page (`src/main/resources/static/index.html`) and a small REST API.

| Endpoint | What it does |
|---|---|
| `GET /api/reviews` | Lists reviews (newest first, email never returned) |
| `POST /api/reviews` | Saves `{name,email,rating(1-5),comment}` |
| `POST /api/contact` | Saves `{name,email,message}` and emails it to you |

## Run locally
```
mvn spring-boot:run
```
Open http://localhost:8080. With no settings it uses a local H2 file database (`./data`).

## Production settings (environment variables)
```
DB_URL=jdbc:postgresql://HOST:5432/portfolio
DB_USER=...   DB_PASSWORD=...
OWNER_EMAIL=you@example.com        # where contact messages are sent
MAIL_USER=you@gmail.com            # SMTP login
MAIL_PASSWORD=...                  # Gmail App Password (not your normal password)
```
Messages are always saved in the `contact_messages` table, even if email fails. Reviews live in `reviews`.

## Read your data
```
SELECT * FROM contact_messages ORDER BY created_at DESC;
SELECT * FROM reviews ORDER BY created_at DESC;
```

## Deploy
1. Push this folder to GitHub.
2. Create a free PostgreSQL database (for example on Neon) and copy its host, database name, user and password.
3. Create a web service from the repo using the included `Dockerfile` (Render, Railway, or any Docker host).
4. Set the environment variables:
```
DB_URL=jdbc:postgresql://HOST/DBNAME?sslmode=require
DB_USER=...   DB_PASSWORD=...
OWNER_EMAIL=...   MAIL_USER=...   MAIL_PASSWORD=...
```
Note: some free hosts block outbound email (SMTP). Messages are still saved in `contact_messages`.
