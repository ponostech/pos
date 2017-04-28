/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers.users;

import Messages.UserMessage;
import controllers.modals.ConfirmDialog;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import jpa.UserJpa;
import org.controlsfx.control.BreadCrumbBar;
import org.controlsfx.control.MaskerPane;
import org.controlsfx.control.Notifications;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import ponospos.entities.User;
import singletons.PonosExecutor;
import util.Role;
import static util.Role.ADMIN;

/**
 *
 * @author Sawmtea
 */
public class UsersController extends StackPane implements 
        ConfirmDialog.ConfirmDialogListener,
        UserDialog.UserDialogListener{
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
    TableColumn <User,Role>roleCol;
    @FXML
    TableColumn <Void,User>actionCol;
    @FXML
    Button newUserBtn;
    @FXML
    BreadCrumbBar crumbBar;
    
    private ObservableList<User>users=FXCollections.observableArrayList();
    private UserDialog userDialog;

    private MaskerPane mask;
    public UsersController(MaskerPane mask) {
        try {
            this.mask=mask;
            FXMLLoader loader=new FXMLLoader();
            loader.setLocation(this.getClass().getResource("/views/users/users.fxml"));
            loader.setController(this);
            Parent root = loader.load();
            this.getChildren().add(root);
            initTable();
        } catch (IOException ex) {
            Logger.getLogger(UsersController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void initTable(){
        usersTable.setItems(users);
        idCol.setCellValueFactory(e->new SimpleStringProperty(String.valueOf(e.getValue().getId())));
        usernameCol.setCellValueFactory(e->new SimpleStringProperty(e.getValue().getUsername()));
        firstnameCol.setCellValueFactory(e->new SimpleStringProperty(e.getValue().getFirstName()));
        lastnameCol.setCellValueFactory(e->new SimpleStringProperty(e.getValue().getLastName()));
        emailCol.setCellValueFactory(e->new SimpleStringProperty(e.getValue().getEmail()));
        roleCol.setCellValueFactory(e->{
            User user = e.getValue();
            Role role;
            if (user.getRole()==0) {
                role=ADMIN;
            }else{
                role=Role.EMPLOYEE;
            }
           return new SimpleObjectProperty(role);
        });
        actionCol.setCellFactory((TableColumn<Void, User> param) -> new TableCell<Void,User>(){
            Button editBtn=new Button("Edit",new Glyph("FontAwesome",FontAwesome.Glyph.EDIT));
            Button delBtn=new Button("Delete",new Glyph("FontAwesome",FontAwesome.Glyph.TRASH));
            
            @Override
            protected void updateItem(User item, boolean empty) {
                if (empty) {
                    setText(null);
                    setGraphic(null);
                }else{
                    editBtn.setOnAction(e->{
                        UserDialog dialog = new UserDialog();
                        dialog.isEditPurpose(true);
                        dialog.setTitle("Update user");
                        dialog.setModel(users.get(getIndex()));
                        dialog.setListener(UsersController.this);
                        dialog.show(UsersController.this);
                    });
                    delBtn.setOnAction(e->{
                        ConfirmDialog dialog=new ConfirmDialog();
                        dialog.setListener(UsersController.this);
                        dialog.setModel(users.get(getIndex()));
                        dialog.show(UsersController.this);
                    });
                    setGraphic(new HBox(editBtn,delBtn));
                }
            } 
        });
        //initialise the content for new user dialog
        
        //initialise dialog for new user entry
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
        CreateUserTask task=new CreateUserTask();
        task.setUser(user);
        task.setOnSucceeded(e->{
            users.add(task.getValue());
            Notifications.create().title(UserMessage.CREATE_SUCCESS_TITLE).text(UserMessage.CREATE_SUCCESS_MESSAGE).showInformation();

        });
        task.setOnFailed(e->{
            
            System.out.println(task.getException().getCause().getMessage());
        });
        mask.visibleProperty().bind(task.runningProperty());
        task.setOnCancelled(e->System.out.println("user creation cancel"));
        PonosExecutor.getInstance().getExecutor().submit(task);
       
    }
    public void updateUser(User user){
        UpdateUserTask task=new UpdateUserTask();
        task.setUser(user);
        mask.visibleProperty().bind(task.runningProperty());
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
        mask.visibleProperty().bind(task.runningProperty());
        task.setOnFailed(e->{
            //TODO:: handle later
            task.getException().printStackTrace();
        });
        PonosExecutor.getInstance().getExecutor().submit(task);
    }
    @FXML
    public void onClickNewUserBtn(ActionEvent e){
        UserDialog d=new UserDialog();
        d.setListener(this);
        d.isEditPurpose(false);
        d.show(this);
    }

    @Override
    public void onClickYes(Object obj) {
        deleteUser((User) obj);
    }

    @Override
    public void onClickCreateButton(User user) {
        createUser(user);
    }

    @Override
    public void onClickUpdateButton(User user) {
        updateUser(user);
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
