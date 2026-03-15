# GetMyShow

A multi-event booking platform built with Spring Boot. Supports booking for movies, sports, concerts, theatre, and standup shows.

## Tech Stack

- **Java 17** / **Spring Boot 3.5**
- **PostgreSQL** + **Flyway** migrations
- **RabbitMQ** for async messaging
- **Gradle** build system

## Modules

| Module | Description |
|---|---|
| `eventcatalog` | Events, shows, venues, screens, and seats |
| `booking` | Seat availability, booking creation, ticketing |
| `user` | User management and roles |
| `payment` | Payment processing |
| `notification` | Notification system |

## Getting Started

### Prerequisites

- Java 17+
- PostgreSQL (port 5433)
- RabbitMQ

### Run

```bash
./gradlew bootRun
```

The app starts on `http://localhost:8080` with the `dev` profile.

### Database

Flyway handles schema migrations automatically on startup. Seed data is included for development.

## API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/shows/{showId}/seats` | Get available seats for a show |
| `POST` | `/api/bookings` | Create a new booking |

Seats are temporarily held for 15 minutes during the booking process.
