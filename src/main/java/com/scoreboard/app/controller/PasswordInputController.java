package com.scoreboard.app.controller;

import com.scoreboard.app.AppContext;
import com.scoreboard.app.model.Account;
import com.scoreboard.app.service.AuthService;
import com.scoreboard.app.view.ViewManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.sasl.AuthenticationException;
import java.time.LocalDateTime;

public class PasswordInputController implements ContextAwareController{
    private static final Logger logger = LoggerFactory.getLogger(PasswordInputController.class);

    AuthService authService;
    private  AppContext context;
    @FXML private TextField passwordTextField;
    @FXML private Label infoLabel;
    @FXML private Label accountNameLabel;
    @FXML private Button submitButton;

    private int failedAttempts = 0;
    private LocalDateTime lockUntil = null;
    private Account pendingAccount;

    @Override
    public void setContext(AppContext context) {
        authService = context.authService();
        this.context = context;

        pendingAccount = context.getPendingAccount();
        accountNameLabel.setText("Login to " + pendingAccount.getName());
        accountNameLabel.setMaxWidth(Double.MAX_VALUE);
    }

    @FXML
    private void initialize() {
        var empty = passwordTextField.textProperty().isEmpty();
        submitButton.disableProperty().bind(empty);

        var tip = new javafx.scene.control.Tooltip("Please input your password");
        submitButton.tooltipProperty().bind(
                javafx.beans.binding.Bindings.when(empty).then(tip).otherwise((javafx.scene.control.Tooltip) null)
        );
    }

    @FXML
    public void submitPassword() throws AuthenticationException {
        infoLabel.setVisible(false);

        if(lockUntil != null && isLoginLocked()) return;

        String passwordInput = passwordTextField.getText();

        if(authService.verifyPassword(pendingAccount.getAccountId(), passwordInput)){
            logger.info("Password matched. Logged in account ID: {}", pendingAccount.getAccountId());
            context.completePendingLogin();
            ViewManager.switchTo("Menu.fxml");
        }else{
            logger.warn("Incorrect password");

            handleLoginFailure();

            infoLabel.setVisible(true);
            infoLabel.setText("Incorrect password.");
        }
    }

    private boolean isLoginLocked() {
        LocalDateTime now = LocalDateTime.now().plusMinutes(5);
        if(lockUntil.isAfter(now)){
            int diff = lockUntil.compareTo(now);
            infoLabel.setVisible(true);
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Account Locked");
            alert.setHeaderText("Too Many Failed Attempts");
            alert.setContentText("Please wait " + diff + " minutes before trying again.");

            alert.showAndWait();
            return true;
        }else{
            return false;
        }
    }

    public void handleLoginFailure() throws AuthenticationException {
        failedAttempts++;

        if (failedAttempts >= 3) {
            lockUntil = LocalDateTime.now().plusMinutes(5);
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Account Locked");
            alert.setHeaderText("Too Many Failed Attempts");
            alert.setContentText(
                    "You have entered an incorrect password 3 times.\n" +
                            "Please wait 5 minutes before trying again."
            );

            alert.showAndWait();
        }

        infoLabel.setVisible(true);
        infoLabel.setText("Password incorrect. " + (3 - failedAttempts) + " times attempts left");
        throw new AuthenticationException(
                "Password incorrect. " + (3 - failedAttempts) + " times attempts left"
        );
    }

    @FXML
    public void resetPasswordTransition(){
        ViewManager.switchTo("SecretQuestion.fxml");
    }

    public void backToList(){
        ViewManager.switchTo("AccountSelect.fxml");
    }
}
