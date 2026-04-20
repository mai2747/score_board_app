package com.scoreboard.app.controller;

import com.scoreboard.app.AppContext;
import com.scoreboard.app.model.Group;
import com.scoreboard.app.model.Player;
import com.scoreboard.app.service.GameService;
import com.scoreboard.app.service.GroupService;
import com.scoreboard.app.view.ViewManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

public class GroupSelectController implements ContextAwareController{
    @FXML private ListView<Group> groupListView;

    private GameService gameService;
    private GroupService groupService;
    private AppContext context;

    @Override
    public void setContext(AppContext context) {
        this.context = context;
        this.gameService = context.gameService();
        this.groupService = context.groupService();
        putGroupList();
    }

    public void putGroupList() {
        ObservableList<Group> groups = FXCollections.observableArrayList(groupService.getAllGroups());

        groupListView.setItems(groups);

        groupListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Group item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                } else {
                    String playerNames = item.getPlayers().stream()
                            .map(Player::getName)
                            .reduce((a, b) -> a + ", " + b)
                            .orElse("");

                    setText(item.getGroupName() + " : " + playerNames);
                }
            }
        });
    }

    @FXML
    public void selectGroup(){
        Group selected = groupListView.getSelectionModel().getSelectedItem();
        context.setSelectedGroupId(selected.getGroupId());

        ViewManager.switchTo("GameSetup.fxml");
    }

    @FXML
    public void createNewGroup(){
        ViewManager.switchTo("GroupSetup.fxml");
    }

    @FXML
    public void backToHome(){
        ViewManager.switchTo("Menu.fxml");
    }

}
