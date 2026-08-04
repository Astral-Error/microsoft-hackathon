# Library Management System

A Spring Boot + Gradle prototype for the VIT Hackathon library scenario. It demonstrates a real lending lifecycle with borrowing request submission, copy-level availability checks, review approval, issuing, cancellation, rejection, and return handling.

## Source Code

The implementation lives under `src/main/java` for backend logic and `src/main/resources` for the Thymeleaf UI, stylesheet, and application configuration.

## Tech Stack

- Java 17
- Spring Boot 3
- Gradle
- Thymeleaf, HTML, CSS, Bootstrap 5
- In-memory persistence for a local prototype

## Run Instructions

### Prerequisites

- JDK 17+
- Gradle installed locally

### Startup

```bash
gradle bootRun
```

### Setup

- Clone or open the repository in VS Code.
- Ensure the JDK and Gradle are available on your machine.
- Run the app with `gradle bootRun`.
- Open `http://localhost:8080` in a browser.

## Assumptions

- The current demo date is fixed to 2026-08-04 so the seeded scenarios match the challenge prompt.
- A borrowing request commits exactly one physical copy when submitted.
- Submitted requests block overlapping requests, reservations, and issued loans for the same copy.
- Review approval creates a confirmed reservation. Issue is a separate action.
- A rejected or cancelled request fully releases the copy.
- Returning a book normally releases the copy; if marked unavailable during return, the copy remains unavailable.
- Copy conditions include available, repair, reference-only, and unavailable states for UI and validation coverage.

## Scope Prioritized

1. Borrowing request creation with validation and copy availability checks.
2. Review, approve, reject, issue, cancel, and return request lifecycle actions.
3. Seeded prototype data that matches the challenge statement.
4. Dashboard metrics and copy status visibility.
5. Clean UI presentation for demonstration in a hackathon setting.

## Implemented Features

- request creation
- field-level form validation
- review approval/rejection
- issue
- cancellation
- return
- seeded data for the prompt
- dashboard metrics and copy status cards
- Bootstrap 5 UI with toasts, modals, status badges, and modern form controls

## Features Excluded

- authentication
- role-based permissions
- external database persistence
- pagination
- alternative-book recommendations
- background notifications

## Important Trade-offs

- In-memory persistence was chosen to keep the prototype fast to run and easy to explain.
- Copy assignment happens at request submission so conflicts are blocked early, even though a full production system might delay allocation.
- The app uses a single-page dashboard instead of separate member and librarian applications to reduce demo complexity.
- The UI prioritizes clarity over heavy interactivity so the workflow is easy to follow in a short presentation.

## Known Limitations

- Data resets when the app restarts because persistence is in memory.
- The demo librarian identity is static and does not represent real login-based access control.
- Copy condition modeling is intentionally lightweight and does not include full repair workflows or status transitions.
- The app is designed for the seeded demo scenario and has not been optimized for large datasets.

## Consulting Pitch

See [pitch.md](pitch.md) for the two-slide consulting pitch covering problem understanding, solution design, trade-offs, validation, and next steps.

## Validation Notes

- The project is designed to be validated with `gradle build` and `gradle bootRun`.
- `gradle build` currently passes locally.
- If your local machine does not have Gradle installed, generate a wrapper or install Gradle first.