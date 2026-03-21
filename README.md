# Scrabble Score Board

⚠️ This project is currently under active development.  
The current implementation focuses on **Milestone 1.2: Enhanced Gameplay and Visualisation**.

---

## Overview

Scrabble Score Board is a desktop-based score management application designed to simplify score tracking during Scrabble games. <br>
The application eliminates manual calculations and provides automatic score aggregation and ranking display to improve gameplay flow.

*This project is a personal educational project and is not affiliated with or endorsed by Hasbro or Mattel. Scrabble is a registered trademark of its respective owners.

---

## Background

Scrabble players often record scores manually on paper or spreadsheets, which can be:

- Time-consuming
- Error-prone
- Disruptive to gameplay

This project aims to automate score management and provide a structured, extendable architecture for future enhancements such as web support and advanced game features.

---

## Documentation

This project follows a specification-driven development process.

- 📄 **Project Specification**  
  [View the specification](docs/Scrabble_Score_Board_specification.pdf)

- 🛠 **Implementation Plan**  
  [View the implementation roadmap](docs/planning/first_implementation_desktop.md)

---

## Current Implementation (Milestone 1.2: Enhanced Gameplay and Visualization)

Milestone 1.2 expands the core gameplay system with score editing, visualization, and basic player/group management. <br>
The following features are implemented:

### Score Management
✅ R1 – Score Input  
Users can input turn scores.

✅ R2 – Automatic Total Calculation  
Total scores are recalculated automatically after each input.

✅ R3 – Score Editing  
Users can edit previously entered scores during gameplay.

### Game Progress & Visualization
✅ R4 – Basic Ranking Display  
Rankings are generated based on total scores (descending order).

✅ R5 – In-game Score Visualization  
Score progression is visualized during gameplay.

✅ R10 – Live-updated Score and Ranking Display  
Scores and rankings update automatically during gameplay.

### Player & Group Management
✅ R7 – Group Registration (Local Only)  
Users can register player groups locally.

✅ R8 – Temporary Group Support  
Users can create a temporary group for one-time gameplay without persistent storage.

✅ R9 – Reordering Players Before a Game  
Players can be reordered before starting a game.

### Game Configuration
✅ R11 – Game Settings  
Basic settings can be configured before starting a game.

### Game Logic
✅ R25 – Automatic Game Termination  
The game ends automatically when all players score zero for three consecutive rounds.


---

## Planned Features (Future Phases)

The following features are defined in the specification but not yet implemented:

- Persistent group storage
- Historical score tracking
- Word-based score calculation (Scrabble rule-based)
- Dictionary validation
- Web migration (Spring Boot + REST API)
- Mobile-friendly UI
- AI opponent

---

## Architecture

The application follows a layered MVC-inspired architecture:

- **Controller Layer** – UI interaction (JavaFX)
- **Service Layer** – Business logic (score calculation, ranking logic)
- **Repository Layer** – Data access abstraction
- **Model Layer** – Domain entities independent of UI and database

Business logic is designed to be UI-independent to support future migration to a web-based architecture.

---

## Tech Stack

### Language
- Java

### UI
- JavaFX

### Database
- SQLite (local)

### Architecture Pattern
- MVC
- Repository Pattern
- Layered Architecture

### Development Tools
- IntelliJ IDEA
- Git / GitHub

---

## How to Run

1. Clone the repository


`git clone https://github.com/mai2747/Score_Calculator.git`


2. Open the project in IDEA

3. Ensure Java version is compatible

4. Run `App.java`

---

## Development Roadmap

Phase 1 – Core scoring system (Current)  
Phase 2 – Web migration using Spring Boot + REST API  
Phase 3 – Extended features (AI, dictionary validation, mobile support)

---

## Design Philosophy

This project emphasises:

- Separation of concerns
- Clear requirement classification (MoSCoW method)
- Incremental development by phase
- Future extensibility toward web and mobile platforms

---

## Status

🟢 Actively developing  
📌 Core scoring system completed  
🚀 Expanding toward web-based architecture
