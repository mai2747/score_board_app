package com.scoreboard.app.controller;

import com.scoreboard.app.AppContext;
import com.scoreboard.app.model.SecQuestion;
import com.scoreboard.app.service.AuthService;
import com.scoreboard.app.view.ViewManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class SecretQuestionController implements ContextAwareController{
    private static final Logger logger = LoggerFactory.getLogger(PasswordInputController.class);
    AuthService authService;

    @FXML Label accountNameLabel;
    @FXML ChoiceBox<SecQuestion> questionChoiceBox;
    @FXML TextField answerTextField;
    @FXML Button submitButton;
    @FXML Label errorLabel;

    @Override
    public void setContext(AppContext context) {
        authService = context.authService();

        String name = authService.getCurrentAccountName();
        accountNameLabel.setText("Secret Question for " + name);
        accountNameLabel.setMaxWidth(Double.MAX_VALUE);

        loadQuestions();
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
    public void submitAnswer(){
        errorLabel.setVisible(false);

        String ans = answerTextField.getText();
        boolean isAnswerCorrect = authService.checkSecretAns(ans);

        if(isAnswerCorrect){
            ViewManager.switchTo("ResetPassword.fxml");
        }else{
            errorLabel.setVisible(true);
            errorLabel.setText("Selected security answer or your answer is incorrect");
        }
    }

    private void loadQuestions() {
        List<SecQuestion> questions = authService.getSecurityQuestions();
        questionChoiceBox.getItems().addAll(questions);
        questionChoiceBox.getSelectionModel().selectFirst();
    }

    @FXML
    public void backToPasswordInput(){
        ViewManager.switchTo("PasswordInput.fxml");
    }
}
