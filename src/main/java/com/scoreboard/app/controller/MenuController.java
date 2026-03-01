package com.scoreboard.app.controller;

import com.scoreboard.app.AppContext;
import com.scoreboard.app.service.GameService;
import com.scoreboard.app.view.ViewManager;
import javafx.fxml.FXML;

// Delete "implements ContextAwareController" if this class does not use any Services
public class MenuController implements ContextAwareController {
    private GameService gameService;

    @Override
    public void setContext(AppContext context) {
        gameService = context.gameService();
    }

    @FXML void createGroupTransition() {
        ViewManager.switchTo("GroupSetup.fxml");
    }
}
