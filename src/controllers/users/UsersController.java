/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers.users;

import Messages.UserMessage;
import controllers.PonosControllerInterface;
import controllers.modals.ConfirmDialog;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.Observable;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import org.controlsfx.control.MaskerPane;
import org.controlsfx.control.Notifications;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import ponospos.entities.User;
import singletons.PonosExecutor;
import tasks.users.CreateTask;
import tasks.users.DeleteTask;
import tasks.users.FetchAllTask;
import tasks.users.UpdateTask;
import util.Role;
import static util.Role.ADMIN;

/**
 *
 * @author Sawmtea
 */
public class UsersController extends AnchorPane implements 
        ConfirmDialog.ConfirmDialogListener,
        UserDialog.UserDialogListener,
        PonosControllerInterface{
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
    Label noOfUserLabel;
    
    private ObservableList<User>users=FXCollections.observableArrayList();
    private UserDialog userDialog;

    private MaskerPane mask;
    private StackPane root;
    public UsersController(MaskerPane mask,StackPane root) {
        try {
            this.mask=mask;
            this.root=root;
            FXMLLoader loader=new FXMLLoader();
            loader.setLocation(this.getClass().getResource("/views/users/users.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        } catch (IOException ex) {
            Logger.getLogger(UsersController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void fetchAllUser(){
        FetchAllTask task=new FetchAllTask();
        task.setOnSucceeded(e->{
            users.clear();
            users.addAll(task.getValue());
            System.out.println(users);
        });
        mask.visibleProperty().bind(task.runningProperty());
        task.setOnCancelled(e->{
            System.out.println(e);
        });
        PonosExecutor.getInstance().getExecutor().submit(task);
    }
    
    private void createUser(User user){
        CreateTask task=new CreateTask();
        task.setUser(user);
        task.setOnSucceeded(e->{
            users.add(task.getValue());
            Notifications.create().title(UserMessage.CREATE_SUCCESS_TITLE).text(UserMessage.CREATE_SUCCESS_MESSAGE).showInformation();

        });
        task.setOnFailed(e->{
            if (task.getException() instanceof SQLException) {
                int errorCode=((SQLException) task.getException()).getErrorCode();
                if (errorCode==1062) {
                    Notifications.create().title(UserMessage.DUPLICATE_USERNAME_TITLE).text(UserMessage.DUPLICATE_USER_MESSAGE).showError();
                }
            }else{
                task.getException().printStackTrace(System.err);
            }
        });
        mask.visibleProperty().bind(task.runningProperty());
        task.setOnCancelled(e->System.out.println("user creation cancel"));
        PonosExecutor.getInstance().getExecutor().submit(task);
       
    }
    public void updateUser(User user){
        UpdateTask task=new UpdateTask();
        task.setUser(user);
        mask.visibleProperty().bind(task.runningProperty());
        task.setOnSucceeded(e->{
            usersTable.refresh();
            Notifications.create().title(UserMessage.UPDATE_SUCCESS_TITLE).text(UserMessage.UPDATE_SUCCESS_MESSAGE).showInformation();
        });
        task.setOnFailed(e->{
            task.getException().printStackTrace();
        });
        PonosExecutor.getInstance().getExecutor().submit(task);
        
    }
    public void deleteUser(User user){
        DeleteTask task=new DeleteTask();
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
        d.show(root);
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

    @Override
    public void initDependencies() {
        
    }

    @Override
    public void initControls() {
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
                        dialog.show(root);
                    });
                    delBtn.setOnAction(e->{
                        ConfirmDialog dialog=new ConfirmDialog();
                        dialog.setListener(UsersController.this);
                        dialog.setModel(users.get(getIndex()));
                        dialog.show(root);
                    });
                    setGraphic(new HBox(editBtn,delBtn));
                }
            } 
        });
    }

    @Override
    public void bindControls() {
        users.addListener((Observable observable) -> {
            noOfUserLabel.setText(Integer.toString(users.size()));
        });
    }

    @Override
    public void hookupEvent() {
        
    }

}
