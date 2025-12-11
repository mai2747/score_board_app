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
|
MainController: Transition interface to ScoreInput
|
===================================================
                [Score Input Interface]
===================================================


ScoreInputController: Display first player's name
|
User inputs score and clicks Submit button
|
ScoreInputController → GameService.submitScore(score)
|
GameService:
1. ScoreService.addScore(score&otherIDs)
   → Create Score object with the info & save to List //will be replaced to DB
2. advanceTurn()
   → Switch to next player
3. Update game state
   → Increment turn number, update currentPlayer
|
ScoreInputController:
1. updatePlayerDisplay()
   → Change Label to display the next player
2. scoreField.clear()
|
*Repeat until the game ends or user clicks Finish Game*
|
User clicks "Finish Game" button
|
GameController → GameService.finishGame()
|
GameService:
1. ScoreService.getTotalScores()
2. RankingService.generateRanking()
3. Pass results to ranking interface and transition to ranking screen

...to be updated
```
