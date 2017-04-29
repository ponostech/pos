/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers.users;

import Messages.RoleMessage;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import ponospos.entities.User;
import singletons.Auth;
import sun.security.util.Password;
import util.PasswordGenerator;

/**
 * FXML Controller class
 *
 * @author Sawmtea
 */
public class ProfileDialog extends JFXDialog {

    public interface ProfileDialogListener{
        public void onProfileChange(User user);
    }
    @FXML
    private Circle avatarCircle;
    @FXML
    private JFXTextField usernameField;
    @FXML
    private JFXTextField firstNameField;
    @FXML
    private JFXTextField lastNameField;
    @FXML
    private JFXPasswordField oldPasswordField;
    @FXML
    private JFXPasswordField newPasswordField;
    @FXML
    private JFXTextField emailField;
    @FXML
    private JFXTextField contactField;
    @FXML
    private JFXButton positivebutton;
    @FXML
    private JFXButton negativeButton;
    @FXML
    private Label profileLabel;
    @FXML
    private Label errorLabel;
    
    private ProfileDialogListener listener;

    /**
     * Initializes the controller class.
     */
   
    public ProfileDialog(ProfileDialogListener listener){
        try {
            this.listener=listener;
            FXMLLoader loader=new FXMLLoader();
            loader.setLocation(this.getClass().getResource("/views/users/profile.fxml"));
            loader.setController(this);
            Region content = loader.load();
            this.setContent(content);
            doBindControls();
            
        } catch (IOException ex) {
            Logger.getLogger(ProfileDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void onPosistivebuttonClick(ActionEvent event) {
        
        User user = Auth.getInstance().getUser();
        user.setUsername(usernameField.getText().trim());
        user.setFirstName(firstNameField.getText().trim());
        user.setLastName(lastNameField.getText().trim());
        user.setPassword(PasswordGenerator.generateToMD5(newPasswordField.getText().trim()));
        user.setEmail(emailField.getText().trim());
        user.setContact(contactField.getText().trim());
        if (! PasswordGenerator.generateToMD5(oldPasswordField.getText())
                .equals(Auth.getInstance().getUser().getPassword())) {
            errorLabel.setText(RoleMessage.INVALID_OLD_PASSWORD_MESSAGE);
            return;
        }
        errorLabel.setText("");    
        listener.onProfileChange(user);
        this.close();
    }

    @FXML
    private void onNegativeButtonClick(ActionEvent event) {
        this.close();
    }
    
    private void doBindControls(){
        positivebutton.disableProperty().bind(
                usernameField.textProperty().isEmpty()
                .or(newPasswordField.textProperty().isEmpty())
                .or(oldPasswordField.textProperty().isEmpty())
        );
    }

    @Override
    public void show(StackPane dialogContainer) {
        applyModel();
        super.show(dialogContainer); 
        
    }
    
    public void applyModel(){
        User user = Auth.getInstance().getUser();
        System.out.println("profile to be change"+user);
        usernameField.setText(user.getUsername());
        firstNameField.setText(user.getFirstName());
        lastNameField.setText(user.getLastName());
        oldPasswordField.setText("");
        newPasswordField.setText("");
        emailField.setText(user.getEmail());
        contactField.setText(user.getContact());
    }
    
    
}
