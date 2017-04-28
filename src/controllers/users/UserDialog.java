/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers.users;

import Messages.UserMessage;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import ponospos.entities.User;
import util.PasswordGenerator;
import util.Role;

/**
 * FXML Controller class
 *
 * @author Sawmtea
 */
public class UserDialog extends JFXDialog {

    
    public interface UserDialogListener{
        public void onClickCreateButton(User user);
        public void onClickUpdateButton(User user);
    }

    @FXML
    private Label topLabel;
    @FXML
    private JFXTextField usernameField;
    @FXML
    private JFXPasswordField passwordField;
    @FXML
    private JFXPasswordField confirmPasswordField;
    @FXML
    private JFXTextField firstnameField;
    @FXML
    private JFXTextField lastnameField;
    @FXML
    private JFXTextField emailField;
    @FXML
    private JFXTextField contactField;
    @FXML
    private JFXComboBox<Role> roleBox;
    @FXML
    private Button positiveBtn;
    
    private UserDialogListener listener;
    private boolean isEditPurpose;
    private User model;
    private SimpleBooleanProperty isValid;
   
    private RequiredFieldValidator requiredValidator;
    private ObservableList userRoles=FXCollections.observableArrayList();
    /**
     * Initializes the controller class.
     */
    
    public UserDialog() {
        try {
            FXMLLoader loader=new FXMLLoader();
            loader.setLocation(this.getClass().getResource("/views/users/user_dialog.fxml"));
            loader.setController(this);
            Parent parent = loader.load();
            this.setContent((Region) parent);
            userRoles.addAll(Role.ADMIN,Role.EMPLOYEE);
            roleBox.setItems(userRoles);
            positiveBtn.setGraphic(new Glyph("FontAwesome", FontAwesome.Glyph.SAVE));
            roleBox.getSelectionModel().selectFirst();
            doValidation();
            bindControls();
        } catch (IOException ex) {
            Logger.getLogger(UserDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void doValidation(){
        positiveBtn.disableProperty().bind(usernameField.textProperty().isEmpty()
                .or(passwordField.textProperty().isEmpty())
                .or(confirmPasswordField.textProperty().isEmpty()));
        
        requiredValidator=new RequiredFieldValidator();
        requiredValidator.setMessage(UserMessage.REQUIRED_MESSAGE);        
        requiredValidator.setIcon(new Glyph("FontAwesome", FontAwesome.Glyph.WARNING).color(Color.RED));
        usernameField.getValidators().add(requiredValidator);
        usernameField.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                usernameField.validate();
            }
            
        });
        passwordField.getValidators().add(requiredValidator);
        passwordField.focusedProperty().addListener((o,oldval,newVal)->{
            if (!newVal) {
                passwordField.validate();
            }
            
        });
        confirmPasswordField.getValidators().add(requiredValidator);
        confirmPasswordField.focusedProperty().addListener((o,oldval,newVal)->{
            if (!newVal) {
                confirmPasswordField.validate();
            }
            
        });
  
        
    }
    public void setTitle(String title) {
        topLabel.setText(title);
    }
    public void setModel(User user){
        this.model=user;
        applyModel();
    }
    public User getModel(){
        return model;
    }
    public void isEditPurpose(boolean value){
        this.isEditPurpose=value;
        if (value) {
            positiveBtn.setText("Update");
        }else{
            positiveBtn.setText("Create");
        }
    }
    public void setListener(UserDialogListener listener){
        this.listener=listener;
    }
    @FXML
    private void onPositiveButtonClick(ActionEvent event) {
        
        if (isEditPurpose){
            model.setUsername(usernameField.getText().trim());
            model.setFirstName(firstnameField.getText().trim());
            model.setLastName(lastnameField.getText().trim());
            model.setEmail(emailField.getText().trim());
            model.setContact(contactField.getText().trim());
            model.setRole((short) roleBox.getSelectionModel().getSelectedItem().ordinal());            
            listener.onClickUpdateButton(model);
            clearAll();
        }else{
            User user=new User();
            user.setUsername(usernameField.getText().trim());
            user.setPassword(PasswordGenerator.generateToMD5(passwordField.getText().trim()));
            user.setFirstName(firstnameField.getText().trim());
            user.setEmail(emailField.getText().trim());
            user.setLastName(lastnameField.getText().trim());
            user.setContact(contactField.getText().trim());
            user.setRole((short) roleBox.getSelectionModel().getSelectedItem().ordinal());
            listener.onClickCreateButton(user);
            clearAll();
            
        }this.close();
    }
    public void clearAll(){
        usernameField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
        emailField.clear();
        firstnameField.clear();
        lastnameField.clear();

    }
    
    public void applyModel(){
        usernameField.setText(model.getUsername());
        emailField.setText(model.getEmail());
        firstnameField.setText(model.getFirstName());
        lastnameField.setText(model.getLastName());
        contactField.setText(model.getContact());
        roleBox.getSelectionModel().select(model.getRole());
                
    }
    private void bindControls(){
        if (isEditPurpose) {
            positiveBtn.disableProperty().bind(usernameField.textProperty().isEmpty());
        }else{
            positiveBtn.disableProperty().bind(
                usernameField.textProperty().isEmpty()
                .or(passwordField.textProperty().isEmpty())
                .or(confirmPasswordField.textProperty().isNotEqualTo(passwordField.textProperty()))
            );
        }
        
    }

}
