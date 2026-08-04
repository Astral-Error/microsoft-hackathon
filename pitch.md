# Slide 1 - Problem Understanding

## Stakeholders / Personas

- Students and researchers who submit borrowing requests.
- Librarians who review, approve, reject, issue, and close requests.
- Library administrators who need visibility into copy usage and blockers.

## Prioritized Workflows

- Submit a borrowing request with a requested date range.
- Prevent submission when no copy is available.
- Review the request before it becomes a reservation or loan.
- Issue an approved reservation.
- Cancel, reject, and return with correct copy release behavior.

## Key Business Rules

- One request is tied to one physical copy.
- Overlapping requests, reservations, and loans cannot share the same copy.
- Unavailable copies are excluded from selection.
- Rejections and cancellations release the copy immediately.
- Returns normally release the copy unless it is marked unavailable during return.

## Scope Chosen for 90 Minutes

- A reliable in-memory prototype with one working end-to-end flow and one failure flow.
- HTML/CSS UI for transparency rather than a complex front-end framework.

# Slide 2 - Solution & Engineering Decisions

## Approach

- Spring Boot MVC with Thymeleaf pages.
- In-memory domain state to keep the prototype runnable without infrastructure.
- Fixed demo clock so the seeded conditions align with the challenge statement.

## Design Decisions

- Treated copy assignment as part of request submission so conflicts are blocked early.
- Kept review, issue, cancel, and return as explicit lifecycle actions.
- Computed dashboard availability from active request periods and operational copy status.

## Trade-offs

- No authentication or RBAC.
- No database persistence.
- No partial fulfillment or alternative-book suggestions.
- No notifications or calendar integration.

## Validation

- Expected validation path is compile, app startup, seeded data inspection, successful request approval/issue/return, and a blocked request scenario.

## Next Improvements

- Add database persistence.
- Add librarian/member login.
- Add audit history and notifications.
- Add search/filtering and richer analytics.