# First Implementation: Desktop Application with JavaFX
### Goal for this implementation
Based on the premise of a single terminal and a single responsible person, create a desktop application that allows a small group of players to record scores turn by turn and automatically calculate total scores within a single game session.

## Milestone 1.1: Core Gameplay (MVP)

### Goal
Implement a minimum playable desktop application that allows two players to record scores turn by turn and view total scores at the end of a game.

### Included Features
- Fixed two-player setup with editable player names
- Turn-based score input
- Automatic total score calculation
- Basic result screen displaying total scores only
- In-memory data storage for a single game session

### Mapped Requirements
- **R1**: Score input
- **R2**: Automatic total calculation
- **R4**: Ranking display (basic, based on total scores only)
- **R8**: Temporary group
- **R25**: Automatic game termination

### Out of Scope
- Score editing
- Graph visualization
- Persistent data storage

### Deliverable
A runnable JavaFX desktop application demonstrating the basic score input and calculation gameplay loop.

---

## Milestone 1.2: Enhanced Gameplay and Visualization

### Goal
Extend the desktop application to support score correction, live-updated gameplay feedback, and in-game score visualization, improving usability and completeness as a score calculator.

### Included Features
- Editing previously entered scores
- Automatic recalculation of total scores and rankings
- In-game score visualization (per turn)
- Live-updated score and ranking display
- Basic game settings (e.g. time limit, pause/resume)

### Mapped Requirements
- **R3**: Score editing
- **R5**: In-game score visualization
- **R7**: Group registration (local only)
- **R9**: Reordering players before a game
- **R10**: Live-updated score and ranking display
- **R11**: Game settings
- **R15**: Pause and resume game
- ~~**R24**: Word-based score calculation~~  // Relocated to Milestone 1.3 (22 Mar 2026)

### Out of Scope
- Database persistence
- Cross-game score history

### Deliverable
A desktop application with editable scores, live-updated rankings, and visualized score progression within a single game.

---

## Milestone 1.3: Database Integration and History Management

### Goal
Replace the in-memory repositories with a SQLite database to enable persistent data storage, reuse of registered groups, and browsing of past game results.

### Included Features
- Persistent storage of players, groups, games, and scores
- Game history table displaying past game results
- Score and ranking history visualization across multiple games
- Comparison of historical performance between players

### Mapped Requirements
- **R6**: Score history table
- **R12**: Score and ranking history visualization
- **R13**: Score history comparison between players
- **R14**: Management of past records (mainly deletion)
- **R24**: Word-based score calculation

### Out of Scope
- Could and Won’t requirements; R16–R23 (*)

### Deliverable
A desktop application with persistent data storage, allowing users to review, analyze, and manage score histories across multiple games.

---
(*)
The could and won’t requirements assume multi-user and multi-device usage, which is better addressed in the second implementation where the system is migrated to a web-based architecture using Spring Boot and REST APIs. \
Board state recording introduces complex UI and rule-processing logic, and is therefore treated as an advanced feature to be implemented after the core scoring functionality is stabilised.

