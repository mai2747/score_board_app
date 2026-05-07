package com.scoreboard.app.controller;

import com.scoreboard.app.App;
import com.scoreboard.app.AppContext;
import com.scoreboard.app.model.Group;
import com.scoreboard.app.view.ViewManager;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import com.scoreboard.app.service.GameService;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class GroupSetupController implements ContextAwareController{
    private static final Logger logger = LoggerFactory.getLogger(GroupSetupController.class);

    @FXML private TextField firstPlayerName;
    @FXML private TextField secondPlayerName;
    @FXML private TextField thirdPlayerName;
    @FXML private TextField fourthPlayerName;
    @FXML private TextField groupName;
    @FXML private VBox playerThree;
    @FXML private VBox playerFour;
    @FXML private Button thirdPlayerButton;
    @FXML private Button fourthPlayerButton;
    @FXML private CheckBox isTemporary;
    @FXML private CheckBox isShowRankingsOn;
    @FXML private Label errorLabel;

    private GameService gameService;
    private AppContext context;

    @Override
    public void setContext(AppContext context){
        this.gameService = context.gameService();
        this.context = context;
    }

    // Further implementation: "Create Group" -> Edit settings -> "Start Game"
    // This method respond to "Create Group"
    @FXML
    private void handleStartButton(ActionEvent event) {
        logger.info("Start button pressed");

        errorLabel.setText("");

        List<String> playerNames = new ArrayList<>();

        if (!addNameOrFail(playerNames, firstPlayerName) || !addNameOrFail(playerNames, secondPlayerName)) {
            errorLabel.setText("Please enter all player names");
            return;
        }

        if (playerThree.isVisible()) {
            if (!addNameOrFail(playerNames, thirdPlayerName)) {
                errorLabel.setText("Please enter all player names");
                return;
            }
        }

        if (playerFour.isVisible()) {
            if (!addNameOrFail(playerNames, fourthPlayerName)) {
                errorLabel.setText("Please enter all player names");
                return;
            }
        }

        for (int i = 0; i < playerNames.size(); i++) {
            logger.debug("Player {}: {}", i + 1, playerNames.get(i));
        }

        Group group = gameService.createNewGroup(context.requireAccountId(), playerNames, groupName.getText(), isTemporary.isSelected());
        context.setSelectedGroupId(group.getGroupId());
        ViewManager.switchTo("GameSetup.fxml");
    }

    private boolean addNameOrFail(List<String> list, TextField field) {
        String name = field.getText() == null ? "" : field.getText().trim();
        if (name.isEmpty()) return false;
        list.add(name);
        return true;
    }

    @FXML
    private void toggleThirdPlayer() {
        boolean showing3 = playerThree.isVisible();

        if (!showing3) {
            // Display Player3
            setNodeVisible(playerThree, true);
            thirdPlayerButton.setText("Delete Player");

            // Display Add Player4 button
            fourthPlayerButton.setVisible(true);
            fourthPlayerButton.setManaged(true);
        } else {
            // Ignore in case Player4 is displayed
            if (playerFour.isVisible()) return;

            // Hide Player3
            setNodeVisible(playerThree, false);
            thirdPlayerButton.setText("Add Player");

            // Hide Add Player4 button
            fourthPlayerButton.setVisible(false);
            fourthPlayerButton.setManaged(false);
        }
        updateButtonsState();
    }

    @FXML
    private void toggleFourthPlayer() {
        boolean showing4 = playerFour.isVisible();

        if (!showing4) {
            // Display Player4
            setNodeVisible(playerFour, true);
            fourthPlayerButton.setText("Delete Player");
        } else {
            // Hide Player4
            setNodeVisible(playerFour, false);
            fourthPlayerButton.setText("Add Player");
        }
        updateButtonsState();
    }

    private void updateButtonsState() {
        boolean player3Shown = playerThree.isVisible();
        boolean player4Shown = playerFour.isVisible();

        // If both player3 player4 are displayed → Disable thirdPlayerButton
        thirdPlayerButton.setDisable(player3Shown && player4Shown);

        // If player3 is not added, player4 button would be hidden
        boolean showFourthBtn = player3Shown;
        fourthPlayerButton.setVisible(showFourthBtn);
        fourthPlayerButton.setManaged(showFourthBtn);

        // If player4 is displayed, fourth button is "Delete"、else "Add"
        fourthPlayerButton.setText(player4Shown ? "Delete Player" : "Add Player");
    }

    private void setNodeVisible(Node node, boolean visible) {
        node.setVisible(visible);
        node.setManaged(visible);
    }

    @FXML
    public void backToHome(){
        ViewManager.switchTo("Menu.fxml");
    }
}
