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
import com.jfoenix.validation.RequiredFieldValidator;
import de.jensd.fx.glyphs.GlyphsBuilder;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import ponospos.entities.User;
import singletons.Auth;
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
    
    @FXML
    private FontAwesomeIconView close;
    
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
            this.setFocusTraversable(true);
            usernameField.requestFocus();
            close.setOnMouseClicked(e->this.close());
            this.setOnDialogOpened(e->usernameField.requestFocus());
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
        
        String userOldPassword = user.getPassword();
        String enteredOldPassword=PasswordGenerator.generateToMD5(oldPasswordField.getText().trim());
        
        user.setPassword(PasswordGenerator.generateToMD5(newPasswordField.getText().trim()));
        user.setEmail(emailField.getText().trim());
        user.setContact(contactField.getText().trim());
      
        if (! userOldPassword.equals(enteredOldPassword)) {
            errorLabel.setText(RoleMessage.INVALID_OLD_PASSWORD_MESSAGE);
            System.out.println("Old password "+userOldPassword);
            System.out.println("Old password enterred "+enteredOldPassword);
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
        doValidate();
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
    public void requestFocusToUsername() {
        usernameField.requestFocus();
    }
    
     public void doValidate(){
        RequiredFieldValidator validator = new RequiredFieldValidator();
        validator.setMessage("Input is Required");
        validator.setIcon(GlyphsBuilder.create(FontAwesomeIconView.class)
            .glyph(FontAwesomeIcon.WARNING)
            .size("1em")
            .styleClass("error")
            .build());
        this.usernameField.setValidators(validator);
        this.usernameField.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            if (!newValue) {
                this.usernameField.validate();
            }
        });
        this.oldPasswordField.setValidators(validator);
        this.oldPasswordField.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            if (!newValue) {
                this.usernameField.validate();
            }
        });
        this.newPasswordField.setValidators(validator);
        this.newPasswordField.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            if (!newValue) {
                this.usernameField.validate();
            }
        });
    }
}
