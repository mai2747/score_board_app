package com.scoreboard.app.controller;

import com.scoreboard.app.AppContext;
import com.scoreboard.app.service.AuthService;
import com.scoreboard.app.view.ViewManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class ResetPasswordController implements ContextAwareController{
    AuthService authService;

    @FXML TextField password1;
    @FXML TextField password2;
    @FXML Label accountName;
    @FXML Label errorLabel;

    @Override
    public void setContext(AppContext context) {
        authService = context.authService();

        accountName.setText(authService.getCurrentAccountName());
    }

    @FXML
    public void submitPassword(){
        errorLabel.setVisible(false);

        String pass1 = password1.getText();
        String pass2 = password2.getText();

        if(pass1.equals(pass2)){
            authService.updatePassword(pass1);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText("""   
                                Settings have been saved.
                                Try your new password to login.
                                
                                You will be sent to account select page.
                                """);
            alert.showAndWait();

            ViewManager.switchTo("AccountSelect.fxml");
        }else{
            errorLabel.setVisible(true);
            errorLabel.setText("Two passwords does not match, try again");
        }
    }
}
