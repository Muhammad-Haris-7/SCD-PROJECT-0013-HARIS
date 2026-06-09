# ⚽ Football Management System

**Semester Project — Software Construction & Development**

A desktop application built with **Java** and **Java Swing** that allows users to manage football teams, players, match schedules, and league standings.

---

## 📋 Table of Contents
1. [Features](#features)
2. [Project Structure](#project-structure)
3. [How to Run](#how-to-run)
4. [Mandatory Concepts Implemented](#mandatory-concepts-implemented)
5. [Screenshots Overview](#screenshots-overview)
6. [Git Commit Guide](#git-commit-guide)

---

## Features

| Module | What you can do |
|---|---|
| **Dashboard** | Live overview — team count, player count, match stats, recent results, top scorers |
| **Teams** | Add, edit, and delete teams; view all stats (W/D/L, GF, GA, GD, Points) |
| **Players** | Add, edit, delete players; update goals, assists, cards; per-player performance score |
| **Matches** | Schedule matches, record results, cancel or delete matches |
| **Standings** | Auto-sorted league table (by points then GD); full top-scorer leaderboard |

---

## Project Structure

```
FootballMS/
├── src/
│   └── football/
│       ├── Main.java                      ← Application entry point
│       ├── model/
│       │   ├── Player.java
│       │   ├── Team.java
│       │   └── Match.java
│       ├── service/
│       │   ├── PlayerService.java         ← Business logic for players
│       │   ├── TeamService.java           ← Business logic for teams
│       │   └── MatchService.java          ← Business logic for matches
│       ├── exception/
│       │   ├── ValidationException.java
│       │   ├── DuplicateEntryException.java
│       │   └── EntityNotFoundException.java
│       ├── util/
│       │   ├── InputValidator.java        ← Centralised validation
│       │   └── SampleDataLoader.java      ← Pre-loads demo data on startup
│       └── ui/
│           ├── MainFrame.java             ← Main window, tab navigation, menu bar
│           ├── DashboardPanel.java
│           ├── TeamPanel.java
│           ├── PlayerPanel.java
│           ├── MatchPanel.java
│           ├── StandingsPanel.java
│           └── UIHelper.java              ← Shared UI utilities
├── test/
│   └── football/service/
│       ├── PlayerServiceTest.java         ← 14 unit tests
│       ├── TeamServiceTest.java           ← 12 unit tests
│       └── MatchServiceTest.java          ← 14 unit tests
├── lib/                                   ← Place JUnit jars here
├── out/                                   ← Compiled classes (auto-created)
├── build_and_run.sh                       ← Linux/macOS build script
├── build_and_run.bat                      ← Windows build script
└── README.md
```

---

## How to Run

### Prerequisites
- **JDK 11 or higher** installed ([Download](https://adoptium.net))
- (Optional for tests) JUnit 4 jars — download links below

### Step 1 — Download JUnit (for unit tests)
Place these two `.jar` files inside the `lib/` folder:

| File | Download |
|---|---|
| `junit-4.13.2.jar` | https://repo1.maven.org/maven2/junit/junit/4.13.2/junit-4.13.2.jar |
| `hamcrest-core-1.3.jar` | https://repo1.maven.org/maven2/org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar |

### Step 2 — Run the build script

**Linux / macOS:**
```bash
chmod +x build_and_run.sh
./build_and_run.sh
```

**Windows:**
```bat
build_and_run.bat
```

The script will:
1. Compile all source files
2. Compile and run all unit tests (if JUnit jars present)
3. Launch the application

### Manual Compilation (without script)
```bash
# Compile
find src -name "*.java" | xargs javac -d out/main

# Run
java -cp out/main football.Main
```

---

## Mandatory Concepts Implemented

### ✅ 1. Event Handling
Every user action is wired to a Swing event listener:
- **Button clicks** — Add, Edit, Delete, Schedule, Record Result, Cancel, Clear
- **Table row selection** — automatically populates the form with the selected record
- **Tab change events** — standings and dashboard refresh when tabs are selected
- **Menu items** — Refresh All, Exit (with confirmation dialog), About

Key files: `TeamPanel.java`, `PlayerPanel.java`, `MatchPanel.java`, `MainFrame.java`

---

### ✅ 2. Exception Handling
Three custom exception types are defined in `football/exception/`:

| Exception | When thrown |
|---|---|
| `ValidationException` | Empty fields, invalid age, negative scores, same team for home and away |
| `DuplicateEntryException` | Adding a player or team with a name that already exists |
| `EntityNotFoundException` | Updating/deleting by an ID that does not exist |

All service methods declare checked exceptions. All UI panels catch these in `try-catch` blocks and display friendly error messages via `JOptionPane`. Runtime exceptions (e.g. `NumberFormatException`) are also caught and converted to user-friendly messages.

Key files: `InputValidator.java`, all `*Service.java`, all UI panels

---

### ✅ 3. Code Refactoring
Several refactoring techniques are applied throughout the project:

- **Single Responsibility** — Models hold data, Services hold business logic, Panels handle UI. No business logic lives in UI classes.
- **Eliminated duplication** — `InputValidator` centralises all validation so each rule is written once. `UIHelper` centralises button creation, dialogs, and form layout.
- **Meaningful naming** — All classes, methods, and variables follow descriptive naming conventions (`applyMatchResult`, `getPerformanceScore`, `populateFormFromSelection`).
- **Modular design** — Each entity (Player, Team, Match) has its own Model, Service, Panel, and Test class.
- **No magic numbers** — Colours are named constants in `UIHelper`; age limits are checked in `InputValidator` with clear labels.

---

### ✅ 4. Unit Testing
**40 unit tests** across three test classes using JUnit 4:

| Test Class | Tests | What is tested |
|---|---|---|
| `PlayerServiceTest` | 14 | Add/update/delete players, stat updates, performance score calculation, top-scorer sorting, team filtering |
| `TeamServiceTest` | 12 | Add/update/delete teams, standings sorting, goal difference, match result application, draw/win/loss logic |
| `MatchServiceTest` | 14 | Schedule matches, record results, cancel matches, duplicate result prevention, upcoming/completed filtering, score display |

Every test method tests a single behaviour. Tests use `@Test(expected = ...)` to assert correct exception types, and `assertEquals`/`assertNotNull` for value assertions.

---

### ✅ 5. Input Validation & Error Messages
Every form validates:
- **Empty fields** — user sees "Player name cannot be empty."
- **Age range** — "Age must be between 15 and 45."
- **Numeric fields** — "Age must be a whole number." (on non-numeric input)
- **Negative scores** — "Score cannot be negative."
- **Duplicate entries** — "A player named 'X' already exists."
- **Same home/away team** — "Home and away teams cannot be the same."
- **Date format** — "Date must be in format dd/MM/yyyy"

---
```

---

## Author
Muhammad Haris— Software Construction & Development, Semester Project

---
*Built with Java 11+ and Java Swing. No external runtime dependencies.*
