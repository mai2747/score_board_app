# First Implementation - Phase 1 Flow (v2)

```
User runs the app 
|
[Menu Interface]
(Allow user to put player names, showing two text field for each of the two players)
|
User clicks Start button
|
MainController → GameService.createNewGroup()
|
GameService:
1. GroupService.createGroup()
   → Create 2-player group (Player1, Player2)
2. Create Game object
3. ((GameRepository.save(game))) *skip DB for this phase
4. Finish first setting of a group and a game
|
MainController: Transition interface to ScoreInput
|
===================================================
                [Score Input Interface]
===================================================

*** Need more updates below (01 Dec 2025) ***

User inputs score and clicks Submit button
|
ScoreInputController → GameService.processTurn(score)
|
GameService:
1. ScoreService.recordScore(score)
   → Create Score object & save to DB
2. TurnOrderUtil.nextTurn()
   → Switch to next player
3. Update game state
   → increment turn number, update currentPlayer
4. Check end-of-game condition (optional for later)
5. Return nextPlayer (or gameFinished flag) to ScoreInputController
|
ScoreInputController:
- If gameFinished = false → update UI for next player
- If gameFinished = true  → transition to result screen
|
*Repeat until the game ends or user clicks Finish Game*
|
User clicks "Finish Game" button
|
GameController:
1. ScoreService.getTotalScores()
2. RankingService.generateRanking()
3. Pass results to ranking interface and transition to ranking screen
```
