/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers.users;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Region;
import jpa.UserJpa;
import ponospos.entities.User;

/**
 *
 * @author Sawmtea
 */
public class UsersController extends Region{
    @FXML
    TableView<User> usersTable;
    @FXML
    TableColumn<User,String> idCol;
    @FXML
    TableColumn <User,String>usernameCol;
    @FXML
    TableColumn <User,String>firstnameCol;
    @FXML
    TableColumn <User,String>lastnameCol;
    @FXML
    TableColumn <User,String>emailCol;
    @FXML
    TableColumn <User,String>roleCol;
    @FXML
    TableColumn <User,Void>actionCol;
    
    private ObservableList<User>users=FXCollections.observableArrayList();

    public UsersController() {
        try {
            FXMLLoader loader=new FXMLLoader();
            loader.setLocation(this.getClass().getResource("/views/users/users.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        } catch (IOException ex) {
            Logger.getLogger(UsersController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void init(){
        usersTable.setItems(users);
        idCol.setCellValueFactory(e->new SimpleStringProperty(String.valueOf(e.getValue().getId())));
        usernameCol.setCellValueFactory(e->new SimpleStringProperty(String.valueOf(e.getValue().getId())));
        firstnameCol.setCellValueFactory(e->new SimpleStringProperty(String.valueOf(e.getValue().getId())));
        lastnameCol.setCellValueFactory(e->new SimpleStringProperty(String.valueOf(e.getValue().getId())));
        emailCol.setCellValueFactory(e->new SimpleStringProperty(String.valueOf(e.getValue().getId())));
        roleCol.setCellValueFactory(e->new SimpleStringProperty(String.valueOf(e.getValue().getId())));
       
    }
    
    private class FetchAllUserTask extends Task<ObservableList<User>>{

        @Override
        protected ObservableList<User> call() throws Exception {
            List<User> all = UserJpa.getAllUsers();
            return (ObservableList<User>) all;
        }
        
    }
    
    
}
