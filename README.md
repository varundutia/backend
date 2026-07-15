# InterviewGPT Backend

InterviewGPT is a Spring Boot backend for an AI-assisted interview practice platform. It supports user registration and JWT login, interview session tracking, answer submission, AI feedback generation through OpenAI, Redis-backed feedback caching, and Stripe checkout/webhooks for paid interview quota.

## Tech Stack

- Java 17
- Spring Boot 3.4.5
- Spring Web, Security, Validation, Data JPA, WebFlux
- PostgreSQL
- Redis
- JWT authentication with `jjwt`
- Stripe Java SDK
- Maven wrapper

## Project Structure

```text
src/main/java/com/interviewgpt/backend
|-- config        # Security, JWT filter, Redis cache/template configuration
|-- controller    # REST endpoints for users, sessions, answers, and payments
|-- dto           # Request DTOs
|-- model         # JPA entities
|-- repository    # Spring Data repositories
|-- service       # Business logic, OpenAI calls, Stripe checkout
`-- utils         # JWT helper
```

## Prerequisites

- JDK 17
- PostgreSQL running locally
- Redis running locally
- OpenAI API key
- Stripe secret key and webhook signing secret

## Configuration

Create or update `src/main/resources/application.properties` with local values:

```properties
spring.application.name=InterviewGPT
server.port=8080

spring.datasource.url=jdbc:postgresql://localhost:5432/interviewgpt
spring.datasource.username=<postgres-user>
spring.datasource.password=<postgres-password>
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

spring.cache.type=redis
spring.data.redis.host=localhost
spring.data.redis.port=6379

jwt.secret=<replace-with-a-strong-secret>
jwt.expiration=3600000

openai.api.key=<openai-api-key>
openai.api.url=https://api.openai.com/v1/chat/completions

stripe.secret.key=<stripe-secret-key>
stripe.webhook.secret=<stripe-webhook-signing-secret>

logging.level.org.springframework=INFO
```

The app uses Hibernate `ddl-auto=update`, so tables are created or updated automatically against the configured database.

## Run Locally

Start PostgreSQL and Redis, then run:

```bash
./mvnw spring-boot:run
```

The API starts on:

```text
http://localhost:8080
```

## Test

```bash
./mvnw test
```

## Authentication

Only these routes are public:

- `POST /api/users/register`
- `POST /api/users/login`
- `POST /api/payments/webhook`

All other routes require:

```http
Authorization: Bearer <jwt-token>
```

## API Overview

### Users

Register a user:

```http
POST /api/users/register
Content-Type: application/json
```

```json
{
  "name": "Ada Lovelace",
  "email": "ada@example.com",
  "password": "password"
}
```

Log in:

```http
POST /api/users/login
Content-Type: application/json
```

```json
{
  "email": "ada@example.com",
  "password": "password"
}
```

Get the authenticated user's email:

```http
GET /api/users/me
Authorization: Bearer <jwt-token>
```

### Interview Sessions

Start a session:

```http
POST /api/sessions/start
Authorization: Bearer <jwt-token>
Content-Type: application/json
```

```json
{
  "title": "Backend Engineer Practice"
}
```

List the authenticated user's sessions:

```http
GET /api/sessions/my-sessions
Authorization: Bearer <jwt-token>
```

Starting a session consumes one record from the user's remaining session quota.

### Answers and AI Feedback

Submit an answer for feedback:

```http
POST /api/answers/submit
Authorization: Bearer <jwt-token>
Content-Type: application/json
```

```json
{
  "questionId": "<interview-question-uuid>",
  "answerText": "My answer..."
}
```

The backend stores the answer, calls OpenAI for feedback, persists the feedback, and caches repeated feedback lookups in Redis for 30 minutes.

### Payments

Create a Stripe checkout session:

```http
POST /api/payments/checkout
Authorization: Bearer <jwt-token>
```

Handle Stripe checkout completion webhooks:

```http
POST /api/payments/webhook
Stripe-Signature: <signature>
```

On `checkout.session.completed`, the webhook increments or creates the user's interview quota.

## Notes

- The Stripe checkout success and cancel URLs are currently hard-coded to `http://localhost:3000/success` and `http://localhost:3000/cancel`.
- Passwords are currently compared directly in the login flow; add password hashing before using this service outside local development.
- There is a service for creating and listing interview questions, but no question controller is exposed yet.
