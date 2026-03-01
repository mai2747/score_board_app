package com.scoreboard.app.controller;

import com.scoreboard.app.AppContext;
import com.scoreboard.app.service.GameService;
import com.scoreboard.app.view.ViewManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

// Delete "implements ContextAwareController" if this class does not use any Services
public class MenuController implements ContextAwareController {
    @FXML Button selectGroupButton;
    @FXML Button settingButton;
    private GameService gameService;

    @FXML
    private void initialize(){
        // Disable buttons not in use
        selectGroupButton.disableProperty();
        settingButton.disableProperty();
    }

    @Override
    public void setContext(AppContext context) {
        gameService = context.gameService();
    }

    @FXML void createGroupTransition() {
        ViewManager.switchTo("GroupSetup.fxml");
    }
}
