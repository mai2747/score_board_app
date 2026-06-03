package com.scoreboard.app.controller;

import com.scoreboard.app.AppContext;
import com.scoreboard.app.model.Account;
import com.scoreboard.app.service.AuthService;
import com.scoreboard.app.view.ViewManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SecretQuestionController implements ContextAwareController{
    private static final Logger logger = LoggerFactory.getLogger(PasswordInputController.class);
    AuthService authService;
    private  AppContext context;
    private Account pendingAccount;

    @FXML Label accountNameLabel;
    @FXML ChoiceBox<Integer> questionList;
    @FXML TextField answerTextField;
    @FXML private Button submitButton;

    @Override
    public void setContext(AppContext context) {
        authService = context.authService();
        this.context = context;

        pendingAccount = context.getPendingAccount();
        accountNameLabel.setText("Secret Question for " + pendingAccount.getName());
        accountNameLabel.setMaxWidth(Double.MAX_VALUE);
    }

    @FXML
    public void initialize(){
        var empty = answerTextField.textProperty().isEmpty();
        submitButton.disableProperty().bind(empty);

        var tip = new javafx.scene.control.Tooltip("Please input your answer");
        submitButton.tooltipProperty().bind(
                javafx.beans.binding.Bindings.when(empty).then(tip).otherwise((javafx.scene.control.Tooltip) null)
        );
    }

    @FXML
    public void backToPasswordInput(){
        ViewManager.switchTo("PasswordInput.fxml");
    }
}
