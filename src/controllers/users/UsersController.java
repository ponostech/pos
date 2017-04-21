/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers.users;

import Messages.UserMessage;
import com.jfoenix.controls.JFXDialog;
import java.io.IOException;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.StackPane;
import jpa.UserJpa;
import org.controlsfx.control.BreadCrumbBar;
import org.controlsfx.control.Notifications;
import ponospos.entities.User;
import singletons.PonosExecutor;

/**
 *
 * @author Sawmtea
 */
public class UsersController extends StackPane{
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
    TableColumn <User,User>actionCol;
    @FXML
    Button newUserBtn;
    @FXML
    BreadCrumbBar crumbBar;
    
    JFXDialog newUserDialog;
    
    private ObservableList<User>users=FXCollections.observableArrayList();
    private CreateUserController createUserController;

    public UsersController() {
        try {
            FXMLLoader loader=new FXMLLoader();
            loader.setLocation(this.getClass().getResource("/views/users/users.fxml"));
            loader.setController(this);
            Parent root = loader.load();
            this.getChildren().add(root);
            init();
        } catch (IOException ex) {
            Logger.getLogger(UsersController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void init(){
        usersTable.setItems(users);
        idCol.setCellValueFactory(e->new SimpleStringProperty(String.valueOf(e.getValue().getId())));
        usernameCol.setCellValueFactory(e->new SimpleStringProperty(String.valueOf(e.getValue().getUsername())));
        firstnameCol.setCellValueFactory(e->new SimpleStringProperty(String.valueOf(e.getValue().getFirstName())));
        lastnameCol.setCellValueFactory(e->new SimpleStringProperty(String.valueOf(e.getValue().getLastName())));
        emailCol.setCellValueFactory(e->new SimpleStringProperty(String.valueOf(e.getValue().getEmail())));
        roleCol.setCellValueFactory(e->new SimpleStringProperty(String.valueOf(e.getValue().getRole())));
        
        //initialise the content for new user dialog
        createUserController=new CreateUserController();
        createUserController.setUserController(this);
        
        //initialise dialog for new user entry
        newUserDialog=new JFXDialog(this, createUserController, JFXDialog.DialogTransition.CENTER);
        //populate usertable by tasks;
        FetchAllUserTask task=new FetchAllUserTask();
        task.setOnSucceeded(e->{
            users.clear();
            users.addAll(task.getValue());
            System.out.println(users);
        });
        task.setOnCancelled(e->{
            System.out.println(e);
        });
        PonosExecutor.getInstance().getExecutor().submit(task);
  
    }
    
    public void createUser(User user){
        //TODO::save user into database
       
        CreateUserTask task=new CreateUserTask();
        task.setUser(user);
        task.setOnSucceeded(e->{
            users.add(task.getValue());
            Notifications.create().title(UserMessage.CREATE_SUCCESS_TITLE).text(UserMessage.CREATE_SUCCESS_MESSAGE).showInformation();

        });
        task.setOnFailed(e->{
            
            System.out.println(task.getException().getCause().getMessage());
        });
        task.setOnCancelled(e->System.out.println("user creation cancel"));
        PonosExecutor.getInstance().getExecutor().submit(task);
         newUserDialog.close();
    }
    public void updateUser(User user){
        UpdateUserTask task=new UpdateUserTask();
        task.setUser(user);
        task.setOnSucceeded(e->{
            users.add(task.getValue());
            Notifications.create().title(UserMessage.UPDATE_SUCCESS_TITLE).text(UserMessage.UPDATE_SUCCESS_MESSAGE).showInformation();
        });
        task.setOnFailed(e->{
            task.getException().printStackTrace();
        });
        PonosExecutor.getInstance().getExecutor().submit(task);
        
    }
    public void deleteUser(User user){
        DeleteUserTask task=new DeleteUserTask();
        task.setUser(user);
        task.setOnSucceeded(e->{
            users.remove(task.getValue());
            Notifications.create().title(UserMessage.DELETE_SUCCESS_TITLE).text(UserMessage.DELETE_SUCCESS_MESSAGE).showInformation();
        });
        task.setOnFailed(e->{
            task.getException().printStackTrace();
        });
        PonosExecutor.getInstance().getExecutor().submit(task);
    }
    @FXML
    public void onClickNewUserBtn(ActionEvent e){
        newUserDialog.show();
    }
    public class UpdateUserTask extends Task<User>{
        private User user;
        @Override
        protected User call() throws Exception {
            return UserJpa.updateUser(user);
        }
        public void setUser(User user){
            this.user=user;
        }
        
    }
    public class DeleteUserTask extends Task<User>{
        private User user;
        @Override
        protected User call() throws Exception {
            return UserJpa.deleteUser(user);
        }
        public void setUser(User user){
            this.user=user;
        }
        
    }
    public class FetchAllUserTask extends Task<List<User>>{

        @Override
        protected List<User> call() throws Exception {
            List<User> all = UserJpa.getAllUsers();
            return all;
        }
        
    }
    
    public class  CreateUserTask extends Task<User>{

        private User user;
        @Override
        protected User call() {
            
            return UserJpa.createUser(user);
            
        }
        public void setUser(User user){
            this.user=user;
        }
    
    }
    
}
