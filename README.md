# Coding Consulting Room Reservation System

A console-based reservation system for a school's coding consulting room / study room, built as a team project and piloted with real students.

**Team project.** This repo is the shared final submission (all four members' code) — see [Credits](#credits) for role breakdown.

## Motivation

The coding consulting room had 16 group-room seats and no way to book them except walking up and hoping a seat was free. The team built a console app to handle reservations, no-shows, and exam-period capacity limits, and piloted it with real student sign-ups.

## Approach

- **Reservation logic:** time-slot booking in 30-minute increments, with conflict checking against existing reservations per seat (`ReservationService.makeReservation`).
- **Resource allocation rules:** max reservation length is 4 hours normally, 3 hours during exam periods (`getMaxHours`); each student can hold only one active reservation at a time; reservations require at least 2 people (self + companions).
- **No-show handling:** a simulated clock (`TimeRuleEngine`) advances in real time-slot increments; a reservation not checked into within one slot of its start time is auto-canceled and logged as a no-show. Three no-shows triggers a usage restriction on that student.
- **Check-in/check-out flow:** reservations move through `RESERVED → IN_USE → DONE` (or `CANCELED`), with a separate admin-only force-return for stuck seats.
- **Roles:** `Student` and `Admin` extend a shared `User`, dispatched via `instanceof` pattern matching in the console UI.

## Tech stack

Java (console I/O only, no external dependencies).

## How to run

```bash
cd src
javac -d ../out Main.java model/*.java service/*.java ui/*.java enums/*.java
java -cp ../out Main
```
Demo accounts are seeded in `Main.initializeData()`: `admin`/`1234` (admin), or any of the seeded student IDs with password `1234`.

## Limitations / what's next

- Console-only — no persistence between runs (all state is in-memory) and no concurrent-user support (single process, single `Scanner`).
- The "time passing" simulation is driven manually rather than by a real clock, which was the right trade-off for a console pilot but wouldn't hold up in an actual scheduled deployment.

## Credits

Team project (4 members). My role: team lead and presenter; implemented the time-slot conflict / resource-allocation logic (`ReservationService`, `TimeRuleEngine`). This repo contains the team's shared final submission. One demo account in `Main.java` originally seeded a real teammate's name and student ID for testing convenience — replaced here with a placeholder before publishing.
