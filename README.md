# Library Management System

A Spring Boot + Gradle prototype for the VIT Hackathon library scenario. It demonstrates a real lending lifecycle with:

- borrowing request submission
- copy-level availability checks before submission
- review approval into a confirmed reservation
- issuing an approved reservation as a loan
- cancellation and rejection releasing the copy
- return handling, including the option to mark a copy unavailable during return

## Tech Stack

- Java 17
- Spring Boot 3
- Gradle
- Thymeleaf, HTML, CSS
- In-memory persistence for a local prototype

## Run

Prerequisites:

- JDK 17+
- Gradle installed locally

Start the app:

```bash
gradle bootRun
```

Open:

```text
http://localhost:8080
```

## Assumptions

- The current demo date is fixed to 2026-08-04 so the seeded scenarios match the challenge prompt.
- A borrowing request commits exactly one physical copy when submitted.
- Submitted requests block overlapping requests, reservations, and issued loans for the same copy.
- Review approval creates a confirmed reservation. Issue is a separate action.
- A rejected or cancelled request fully releases the copy.
- Returning a book normally releases the copy; if marked unavailable during return, the copy remains unavailable.

## Scope

Implemented:

- request creation
- review approval/rejection
- issue
- cancellation
- return
- seeded data for the prompt
- dashboard metrics and copy status cards

Excluded:

- authentication
- role-based permissions
- notifications
- external database persistence
- pagination
- alternative-book recommendations

## Consulting Pitch

See [pitch.md](pitch.md).

## Validation Notes

- The project is designed to be validated with `gradle build` and `gradle bootRun`.
- If your local machine does not have Gradle installed, generate a wrapper or install Gradle first.