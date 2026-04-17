# Scrabble Score Board specification

---

---

## 1. Introduction

This document outlines the specifications for a Scrabble score management application and provides guidelines for design decisions and implementation steps throughout the development process.

The primary intended reader of this specification is the application's developer, including the author of future versions. Additionally, this document is intended for readers with a technical background, such as interviewers or engineers reviewing the project, who wish to understand the design rationale and scope of implementation.

The current implementation focuses on desktop and browser-based applications for recording and calculating Scrabble scores. Advanced features such as real-time multiplayer gameplay, AI opponents and mobile support have been excluded from the current scope. These features may be addressed in later iterations of the project and are considered potential future enhancements.

---

## 2. Background and Goals

### Problem:
Scrabble players typically record their scores on paper and calculate totals manually for each game, which is time-consuming and error-prone. When using spreadsheets such as Excel, players must manually prepare calculation formulas and clear previous scores at the start of every game, resulting in additional setup effort and interruptions to gameplay.

### Goal:
Develop an application that Scrabble players easily record, calculate and visualise their game scores.

Key features include:
- Automate a calculation of score on app
- User-based historical score tracking
- Graph visualisation of score

### Stakeholders:
Player, developer

---

## 3. Implementation Plan

The implementation of this app is divided into three steps.

The first implementation focuses on creating basic functions in a local environment. The second implementation involves transferring Java logic to Spring Boot to create an API. The app will be published and made available on mobile phones in the third implementation.

### First Implementation:
Programming language: Java  
Development environment: IntelliJ  
UI framework: JavaFX  
Architecture: MVC  
Database: SQLite  
Version control: GitHub

### Second Implementation:
Programming language: Java, JavaScript  
Development environment: IntelliJ, VS Code  
UI framework: HTML, CSS, JavaScript  
Architecture: Spring Boot (Controller/Service/Model) + REST API  
Database: Spring Boot + SQLite  
Version control: GitHub  
Graph rendering: Chart.js on HTML  
Hosting: local

### Third Implementation:
Database Firebase Firestore or Supabase  
Hosting Own domain

---

## 4. Functional Requirements

### Must: Core functions for storing and calculating scores.

#### ID 1
**User Story:**  
As a user, I want the system to be able to read score that I input, so that I can record my score.

**Frontend:**  
A front-end interface must allow users to input their score as an integer.

**Backend:**  
The backend must accept the score input sent from the frontend and validate it (e.g., type checking, business rules).  
The validated score should be stored in a database table, associated with the correct user and game session if applicable.

---

#### ID 2
**User Story:**  
As a user, I want the system to automatically calculate the recorded scores, so that I don’t have to do it manually.

**Backend:**  
The backend must calculate the total scores of all players using data from the score table.  
The calculation should be triggered automatically whenever new scores are added or existing scores are updated.

---

#### ID 3
**User Story:**  
As a user, I want to be able to edit previously entered scores, so that I can correct any mistakes.

**Frontend:**  
A button or interface element must allow the user to select and edit a previously entered score

**Backend:**  
The backend must update the corresponding score in the database table when a user edits it.  
The system must also recalculate the total score to reflect the updated value.

---

#### ID 4
**User Story:**  
As a user, I want to see the rankings based on scores, so that I can understand the current standings.

**Frontend:**  
The frontend must display usernames and scores in descending order based on their ranking.

**Backend:**  
The backend must generate a ranking by sorting users according to their total scores.

---

#### ID 5
**User Story:**  
As a user, I want to see score progression within a single game, so that I can visually track how scores change turn by turn.

**Frontend:**  
Line graph must display each player’s score progression over time.

**Backend:**  
The backend should provide structured score data per player (e.g., by round or turn) to support line graph generation.  
The data must be formatted in a way that allows easy plotting on the frontend.

---

#### ID 6
**User Story:**  
As a user, I want to view a list of my past games, including total scores and rankings per game, so that I can review my performance history.

**Frontend:**  
A dedicated page or section must be implemented to show a user's score and ranking history.  
This page should display a table of total scores and ranking per game, sorted by date.

**Backend:**  
The backend must store historical score data and ranking per user in the database.

---

#### ID 7
**User Story:**  
As a user, I want to be able to register or select a group, so that our game data can be saved and managed as a unit.

**Frontend:**  
A button or interface element must allow the user to select or register a new group with a set of fixed members.  
Group information (group name, member names) must be input during creation.

**Backend:**  
The backend must store player information in the database.  
All future score data must be associated with the registered group.

---

#### ID 8
**User Story:**  
As a user, I want to create a temporary group for one-time play, so that we can enjoy a game session without saving any data.

**Frontend:**  
A button or interface element must allow the user to create a temporary group.  
Member names should be input during creation. (“Player ”+number by default)

**Backend:**  
The backend must support gameplay using temporary groups but should not store their score data persistently.

---

#### ID 9
**User Story:**  
As a user, I want to change the play order in a new game, so that I can customize the sequence of turns.

**Frontend:**  
A button must allow the user to rearrange the order of players before the game starts.

**Backend:**  
This order must be used when initialising the score table and determining turn sequence.

---

#### ID 10
**User Story:**  
As a user, I want the system to display the current score and ranking, so that I can stay engaged and enjoy the competition.

**Frontend:**  
The current score and ranking must be displayed in real-time during gameplay.  
A toggle should allow users to manually show or hide the score and ranking display.  
The system should have an option of hiding the score and ranking automatically once a certain number of pieces have been placed.

**Backend:**  
The backend must maintain the current total scores for each player.  
Rankings must be dynamically generated based on those total scores.

---

#### ID 25
**User Story:**  
As a user, I want the game to end automatically when all players score zero for three consecutive rounds, so that the game follows official Scrabble endgame rules.

**Frontend:**  
Display the current count of consecutive zero-score rounds.  
When the game ends due to consecutive zero-score rounds, display a clear game-over message explaining the reason.

**Backend:**  
Track consecutive zero-score turns for each player.  
Detect when all players have scored zero for a number of consecutive turns equal to the number of players over three full rounds.  
Trigger a game-over event and notify the UI layer when the condition met.

---

## 5. Non-Functional Requirements

### Performance
- Score totals must be updated immediately after each score input.
- The application should respond to user input within a perceptible timeframe.

### Persistence & Reliability
- Game and score data must be persisted using a local SQLite database.
- Stored data must remain available after the application is closed and restarted.

### Maintainability
- The application must follow a layered architecture separating UI, service, and data access logic.
- Business logic must not depend on UI-specific frameworks.

### Usability
- The score input process should be simple enough to be completed during gameplay without interrupting the flow.
- Player names and turn order must be clearly visible during score entry.

---

## 6. Screen Transitions

### Menu Scene
Serves as the main entry point of the application and guides users to major sections.

**Trigger Next Scene**
- Select “Create Group” → Group Setup Scene
- Select “Select Group” → Group Selection Scene
- Select “Settings” → Setting Scene

---

### Group Setup Scene
Allows users to define the number of players and configure player names.

**Trigger Next Scene**
- Select “Next” → Game Setup Scene
- Select “Back” → Confirmation dialog displayed
- Confirm “Discard changes” → Menu Scene
- Cancel confirmation → Remain on Group Setup Scene

---

### Group Selection Scene
Allows users to select a previously saved group.

**Trigger Next Scene**
- Select “Edit Group Info” → Group Setup Scene
- Select “Next” → Game Setup Scene
- Select “Back” → Menu Scene

---

### Game Setup Scene
Allows users to configure game-specific settings before starting a game.

**Trigger Next Scene**
- Select “Start Game” → Score Input Scene
- Select “Back” → Group Setup Scene

---

### Score Input Scene
Allows users to input scores turn by turn during gameplay.

**Trigger Next Scene**
- Select “Finish Game” → Ranking Scene
- Select “Pause” → Pause dialog displayed
- Select “Quit Game” (from pause) → Menu Scene
- Select “Back” (from pause) → Resume Score Input Scene

---

### Ranking Scene
Displays the final results of the game, including total scores and player rankings.

**Trigger Next Scene**
- Select “Back to Menu” → Menu Scene
- Select “Chart” → Chart View Scene
- Select “Another Game” → Game Setup Scene

---

### Chart View Scene
Displays detailed score progression using charts and tables.

**Trigger Next Scene**
- Select “Back to Menu” → Menu Scene
- Select “See Rankings” → Ranking Scene

---

### Setting Scene
Allows users to manage application and data-related settings.

(To be defined)

---

## 7. Revisions

### v.0.2.0 - Background of Reclassification
Requirements reorganised based on technical concerns instead of MoSCoW only.

### v.0.3.0 – Additional requirements
- Word-based score calculation (R24)
- Automatic Game Termination (R25)  