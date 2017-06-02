/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import Messages.LoginMessages;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javax.persistence.NoResultException;
import jpa.UserJpa;
import ponospos.PonosPos;
import ponospos.entities.Stores;
import ponospos.entities.User;
import singletons.Auth;
import singletons.PonosExecutor;
import tasks.stores.FetchAllTask;
import util.PasswordGenerator;

/**
 * FXML Controller class
 *
 * @author Sawmtea
 */
public class LoginController extends StackPane {
    @FXML
    JFXComboBox<Stores> storesCombo;
    @FXML
    JFXTextField usernameField;
    @FXML
    JFXPasswordField passwordField;
    @FXML
    JFXButton loginButton;
    @FXML
    JFXButton exitButton;
    @FXML
    Label errorLabel;
    
    private ObservableList<Stores> stores=FXCollections.observableArrayList();
    private PonosPos app;
    
    public LoginController() {
        try {
            FXMLLoader loader=new FXMLLoader();
            loader.setLocation(this.getClass().getResource("/views/login_fxml.fxml"));
            loader.setController(this);
            Parent parent=loader.load();  
            this.getChildren().add(parent);
            this.getStyleClass().add("login-root-container");
            init();
            loginButton.disableProperty().bind(
                 usernameField.textProperty().isEmpty().or(
                         passwordField.textProperty().isEmpty()
                 ).or(storesCombo.getSelectionModel().selectedItemProperty().isNull())
            );
        } catch (IOException ex) {
            Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void init(){
        storesCombo.setItems(stores);
        FetchAllTask task=new FetchAllTask();
        task.setOnSucceeded(e->{
            stores.clear();
            stores.addAll(task.getValue());
        });
        task.setOnFailed(e->task.getException().printStackTrace(System.err));
        PonosExecutor.getInstance().getExecutor().submit(task);
        
    }

    public void setApp(PonosPos app) {
        this.app = app;
        hookUpEvent();
    }
    private void hookUpEvent(){
        loginButton.setOnAction(e->{
            String username=usernameField.getText().trim();
            String password=passwordField.getText().trim();
            if (isValidInput(username,password)) {
                LoginTask task=new LoginTask();
                task.setUsername(username);
                task.setPassword(PasswordGenerator.generateToMD5(password));
                task.setOnFailed(value->{
                    if (task.getException() instanceof NoResultException) {
                        errorLabel.setText(LoginMessages.WRONG_CREDENTIAL);
                    }
                    task.getException().printStackTrace(System.err);
                    });
                task.setOnSucceeded(event->{
                    if (task.getValue()!=null) {
                        errorLabel.setText("");
                        usernameField.clear();
                        passwordField.clear();
                        Auth.getInstance().setUser(task.getValue());
                        Auth.getInstance().setIsLogged(true);
                        Auth.getInstance().setLoginStore(storesCombo.getSelectionModel().getSelectedItem());
                        System.out.println("logged user password "+Auth.getInstance().getUser().getPassword());
                        app.displayMainScreen();
                    }else{
                        errorLabel.setText(LoginMessages.WRONG_CREDENTIAL);
                    }
                });
                PonosExecutor.getInstance().getExecutor().submit(task);
            }else{
                errorLabel.setText(LoginMessages.INVALID_INPUT);
            }
            
        });
        exitButton.setOnAction(e->{
            try {
                System.exit(0);
            } catch (Exception ex) {
                Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
    }

    private boolean isValidInput(String username, String password) {
        return !(username.isEmpty() || password.isEmpty() || storesCombo.getSelectionModel().getSelectedItem()==null);
    }

    
    
    private class LoginTask extends Task<User>{
        private String username;
        private String password;
        @Override
        protected User call() throws Exception {
            return UserJpa.checkCredential(username, password);
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public void setPassword(String password) {
            this.password = password;
        }
        
    }
}
