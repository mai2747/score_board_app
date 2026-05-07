package com.scoreboard.app.controller;

import com.scoreboard.app.AppContext;
import com.scoreboard.app.model.Account;
import com.scoreboard.app.service.AuthService;
import com.scoreboard.app.view.ViewManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

public class AccountSelectController implements ContextAwareController {
    private AppContext context;
    private AuthService authService;

    @FXML private ListView<Account> accountListView;

    private Account pendingAccount;

    @Override
    public void setContext(AppContext context) {
        this.context = context;
        this.authService = context.authService();
        putAccountList();
    }

    @FXML
    public void initialize() {
        accountListView.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, oldVal, newVal) -> {
                    if (newVal != null) {
                        pendingAccount = newVal;
                    }
                });
    }

    public void putAccountList() {
        ObservableList<Account> accounts = FXCollections.observableArrayList(authService.getAllAccount());

        accountListView.setItems(accounts);

        accountListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Account item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName());
                }
            }
        });
    }

    @FXML
    public void selectAccount() {
        Account selected = accountListView.getSelectionModel().getSelectedItem();

        if (selected != null) {
            this.pendingAccount = selected;
            context.setPendingAccount(selected);
            ViewManager.switchTo("PasswordInput.fxml");
        }
    }

    @FXML
    public void loginAsGuest() {
        Account guestAccount = authService.createGuestAccount();
        context.login(guestAccount);
        context.setGuestMode(true);
    }

    @FXML
    public void createNewAccount() {
        ViewManager.switchTo("AccountSetup.fxml");
    }
}
