package com.scoreboard.app.controller;

import com.scoreboard.app.AppContext;
import com.scoreboard.app.Exception.ValidationException;
import com.scoreboard.app.model.Account;
import com.scoreboard.app.model.SecurityQuestion;
import com.scoreboard.app.service.AuthService;
import com.scoreboard.app.validation.InputValidator;
import com.scoreboard.app.view.ViewManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.List;

public class AccountSetupController implements ContextAwareController{
    AuthService authService;
    AppContext context;
    @FXML private TextField accountNameTextField;
    @FXML private TextField secretQuestionTextField;
    @FXML private TextField passwordTextField;
    @FXML private TextField passwordConfirmationTextField;
    @FXML private Label errorLabel;
    @FXML private ChoiceBox<SecurityQuestion> questionChoiceBox;

    @Override
    public void setContext(AppContext context) {
        this.authService = context.authService();
        this.context = context;

        loadQuestions();
    }

    @FXML
    public void registerAccount() {
        errorLabel.setVisible(false);
        try {
            Account account = validateInputs();

            authService.createAccount(account);
            context.login(account);
            ViewManager.switchTo("Menu.fxml");

        } catch (ValidationException e) {
            showValidationError(e.getMessage());
        }
    }

    // TODO: validate it, currently this method just create Account model
    private Account validateInputs() throws ValidationException {
        SecurityQuestion selected = questionChoiceBox.getValue();

        if (selected == null) {
            // 未選択エラー
            return null;
        }

        String accountName = requireText(accountNameTextField, "Account name");
        int questionId = selected.getId();
        String secretQuestionAnswer = requireText(secretQuestionTextField, "Secret question answer");
        String password = requireText(passwordTextField, "Password");
        String passwordConfirmation = requireText(passwordConfirmationTextField, "Password confirmation");

        String validatedAccountName = InputValidator.validateAccountName(accountName);
        String validatedSecretQuestionAnswer = InputValidator.validateSecretQuestionAnswer(secretQuestionAnswer);
        String validatedPassword = InputValidator.validatePassword(password);
        String validatedPasswordConfirmation =
                InputValidator.validatePasswordConfirmation(passwordConfirmation, validatedPassword);

        return new Account(
                validatedAccountName,
                authService.hashPassword(validatedPassword),
                questionId,  // default
                authService.hashSecurityAnswer(validatedSecretQuestionAnswer)
        );
    }

    private String requireText(TextField field, String fieldName) throws ValidationException {
        String text = field.getText();

        if (text == null || text.trim().isEmpty()) {
            errorLabel.setVisible(true);
            errorLabel.setText(fieldName + " is required.");
            throw new ValidationException(fieldName + " is required.");
        }

        return text;
    }

    private void loadQuestions() {
        List<SecurityQuestion> questions = authService.getSecurityQuestions();
        questionChoiceBox.getItems().addAll(questions);
        questionChoiceBox.getSelectionModel().selectFirst(); // 初期選択
    }

    private void showValidationError(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Invalid Input");
        alert.setHeaderText("Could not register account.");
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void backToAccountSelection(){
        ViewManager.switchTo("AccountSelect.fxml");
    }
}
