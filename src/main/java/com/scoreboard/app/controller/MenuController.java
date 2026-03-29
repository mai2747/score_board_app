package com.scoreboard.app.controller;

import com.scoreboard.app.AppContext;
import com.scoreboard.app.service.GameService;
import com.scoreboard.app.view.ViewManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

// Delete "implements ContextAwareController" if this class does not use any Services
public class MenuController implements ContextAwareController {
    @FXML Button selectGroupButton;
    @FXML Button settingButton;
    @FXML Button pausedGameButton;
    @FXML VBox buttonVBox;

    private GameService gameService;

    @FXML
    private void initialize(){
        settingButton.setDisable(true);
    }

    @Override
    public void setContext(AppContext context) {
        gameService = context.gameService();

        boolean hasPausedGame = gameService.hasPausedGame();
        pausedGameButton.setVisible(hasPausedGame);
        pausedGameButton.setManaged(hasPausedGame);
    }

    @FXML void createGroupTransition() {
        ViewManager.switchTo("GroupSetup.fxml");
    }

    @FXML void selectGroupTransition(){ ViewManager.switchTo("GroupSelect.fxml"); }

    @FXML void pausedGameTransition(){ ViewManager.switchTo("PausedGame.fxml"); }

    @FXML void settingTransition(){ ViewManager.switchTo("Setting.fxml"); }
}
