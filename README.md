# Coding Consulting Room Reservation System

A console-based reservation system for a school's coding consulting room and study room, built as a team project and piloted with real students.

**Team project.** This repo is the shared final submission, containing all four members' code. See [Credits](#credits) below for who did what.

## Motivation

The coding consulting room had 16 group-room seats and no real way to book them, other than walking up and hoping a seat happened to be free. The team built a console app to handle reservations, no-shows, and reduced capacity during exam periods, and piloted it with real student sign-ups.

## Approach

**Reservation logic.** Seats are booked in 30-minute time slots, with conflict checking against existing reservations for that same seat (`ReservationService.makeReservation`).

**Resource allocation rules.** A reservation can normally run up to 4 hours, or up to 3 hours during exam periods (`getMaxHours`). Each student can hold only one active reservation at a time, and every reservation needs at least 2 people (the student plus at least one companion).

**No-show handling.** A simulated clock (`TimeRuleEngine`) advances in the same real time-slot increments as the reservations. If a reservation isn't checked into within one slot of its start time, it's automatically canceled and logged as a no-show. After three no-shows, a student's usage is restricted.

**Check-in and check-out flow.** A reservation moves through the states `RESERVED`, then `IN_USE`, then `DONE` (or `CANCELED`). There's also a separate, admin-only option to force-return a seat that's stuck.

**Roles.** `Student` and `Admin` both extend a shared `User` class, and the console UI tells them apart using `instanceof` pattern matching.

## Tech stack

Java, using only console input and output, with no external dependencies.

## How to run it

```bash
cd src
javac -d ../out Main.java model/*.java service/*.java ui/*.java enums/*.java
java -cp ../out Main
```

Demo accounts are seeded in `Main.initializeData()`. You can log in as the admin account (`admin` / `1234`), or as any of the seeded student IDs with the password `1234`.

## Limitations and what's next

This is console-only, so nothing is saved between runs (all state lives in memory), and it doesn't support multiple users at once, since it runs as a single process reading from a single `Scanner`.

The passage of time is simulated manually rather than driven by a real clock. That was the right trade-off for a console pilot, but it wouldn't hold up in an actual scheduled, always-on deployment.

## Credits

Team project with 4 members. My role was team lead and presenter, and I implemented the time-slot conflict and resource-allocation logic (`ReservationService`, `TimeRuleEngine`). This repo contains the team's shared final submission. One demo account in `Main.java` originally used a real teammate's name and student ID for testing convenience; it's been replaced here with a placeholder before publishing.
