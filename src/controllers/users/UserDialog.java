/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers.users;

import Messages.UserMessage;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import com.jfoenix.validation.ValidationFacade;
import com.jfoenix.validation.base.ValidatorBase;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import org.controlsfx.validation.ValidationSupport;
import ponospos.entities.User;
import util.PasswordGenerator;
import util.Role;

/**
 * FXML Controller class
 *
 * @author Sawmtea
 */
public class CreateUserController extends VBox {

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
    private Button saveBtn;
    
   
    private RequiredFieldValidator requiredValidator;
    private UsersController userController;
    private ValidationSupport validationSupport;

    private ObservableList userRoles=FXCollections.observableArrayList();
    /**
     * Initializes the controller class.
     */
    
    public CreateUserController() {
        try {
            FXMLLoader loader=new FXMLLoader();
            loader.setLocation(this.getClass().getResource("/views/users/create_user.fxml"));
            loader.setController(this);
            Parent parent = loader.load();
            this.getChildren().add(parent);
            userRoles.addAll(Role.ADMIN,Role.EMPLOYEE);
            roleBox.setItems(userRoles);
            roleBox.getSelectionModel().selectFirst();
            saveBtn.setGraphic(new Glyph("FontAwesome", FontAwesome.Glyph.SAVE));
            doValidation();
        } catch (IOException ex) {
            Logger.getLogger(CreateUserController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setUserController(UsersController userController) {
        this.userController = userController;
    }
    

    private void doValidation(){
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
    @FXML
    private void onSaveButtonClick(ActionEvent event) {
        User user=new User();
        user.setUsername(usernameField.getText().trim());
        user.setPassword(PasswordGenerator.generateToMD5(passwordField.getText().trim()));
        user.setEmail(emailField.getText().trim());
        user.setFirstName(firstnameField.getText().trim());
        user.setLastName(lastnameField.getText().trim());
        user.setContact(contactField.getText().trim());
        user.setRole((short) roleBox.getSelectionModel().getSelectedItem().ordinal());
        userController.createUser(user);
    }
    
}
