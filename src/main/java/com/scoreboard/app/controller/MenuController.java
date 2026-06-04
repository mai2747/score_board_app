package com.scoreboard.app.controller;

import com.scoreboard.app.AppContext;
import com.scoreboard.app.service.AuthService;
import com.scoreboard.app.service.GameService;
import com.scoreboard.app.view.ViewManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

// Delete "implements ContextAwareController" if this class does not use any Services
public class MenuController implements ContextAwareController {
    private static final Logger logger = LoggerFactory.getLogger(MenuController.class);

    @FXML Button selectGroupButton;
    @FXML Button historyButton;
    @FXML Button pausedGameButton;
    @FXML VBox buttonVBox;
    @FXML Label accountNameLabel;

    boolean hasPausedGame;
    private GameService gameService;
    private AuthService authService;

    @Override
    public void setContext(AppContext context) {
        gameService = context.gameService();
        authService = context.authService();

        String accountName = authService.requireAccount().getName();
        accountNameLabel.setText(accountName);

        refreshPausedGameState();
    }

    private void refreshPausedGameState() {
        String pausedGameName = gameService.getPausedGameGroupName();
        hasPausedGame = (pausedGameName != null);
        logger.debug("Having paused games? {}", hasPausedGame);

        if (hasPausedGame) {
            pausedGameButton.setMinWidth(Region.USE_PREF_SIZE);
            pausedGameButton.setText(pausedGameName + "'s game is paused, resume?");
        }

        pausedGameButton.setVisible(hasPausedGame);
        pausedGameButton.setManaged(hasPausedGame);
    }

    @FXML void createGroupTransition() {
        if(!hasPausedGame || canProceedToNewGame()) {
            ViewManager.switchTo("GroupSetup.fxml");
        }
    }

    @FXML void selectGroupTransition(){
        if(!hasPausedGame || canProceedToNewGame()) {
            ViewManager.switchTo("GroupSelect.fxml");
        }
    }

    private boolean canProceedToNewGame(){
        if(!hasPausedGame) return true;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm");
        alert.setHeaderText("Start new game?");
        alert.setContentText("If you start a new game, the paused game will be cancelled.");

        Optional<ButtonType> result = alert.showAndWait();

        return result.isPresent() && result.get() == ButtonType.OK;
    }

    @FXML void pausedGameTransition(){ ViewManager.switchTo("PausedGame.fxml"); }

    @FXML void historyTransition(){ ViewManager.switchTo("HistoryGroupSelect.fxml"); }

    @FXML void logout(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm");
        alert.setHeaderText("Logout?");
        alert.setContentText("Push OK to back to account selection page.");

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isEmpty() || result.get() != ButtonType.OK) {
            return;
        }

        authService.logout();
        ViewManager.switchTo("AccountSelect.fxml");
    }
}
