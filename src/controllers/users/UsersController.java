/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers.users;

import Messages.ConfirmationMessage;
import Messages.UserMessage;
import controllers.PonosControllerInterface;
import controllers.modals.ConfirmDialog;
import java.io.IOException;
import java.util.List;
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
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Window;
import javax.persistence.RollbackException;
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
            if (task.getException() instanceof RollbackException){
                  Notifications.create().title(UserMessage.DUPLICATE_USERNAME_TITLE).text(UserMessage.DUPLICATE_USER_MESSAGE).showError();
            }else{
                System.out.println("Exception in handle in here");
                 Notifications.create().title(UserMessage.DUPLICATE_USERNAME_TITLE).text(UserMessage.DUPLICATE_USER_MESSAGE).showError();
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
        this.usersTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
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
                        ConfirmDialog dialog=new ConfirmDialog(ConfirmationMessage.TITLE,ConfirmationMessage.MESSAGE);
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
        usersTable.setRowFactory(p->{
            TableRow<User> row=new TableRow();
            
            row.setOnMouseClicked(e->{
                User selectedItem=usersTable.getSelectionModel().getSelectedItem();
                if ( e.getButton()==MouseButton.SECONDARY) {
                    displayPopOver(e.getScreenX(),e.getScreenY(),selectedItem);
                }else if(e.getClickCount()==2){
                    User user = users.get(row.getIndex());
                    viewUser(user);
                }else{
                    //do nothing
                }
            });
            return row;
        });
    }

    private void displayPopOver(double x,double y,User selectedItem){
        MenuItem edit=new MenuItem("Edit",new Glyph("FontAwesome", FontAwesome.Glyph.EDIT).color(Color.CORAL).size(22));
        MenuItem del=new MenuItem("Delete");
        del.setGraphic(new Glyph("FontAwesome", FontAwesome.Glyph.TRASH).color(Color.CORAL).size(22));
        del.setOnAction(e->{
           ConfirmDialog d=new ConfirmDialog(ConfirmationMessage.TITLE,ConfirmationMessage.MESSAGE);
           d.setModel(selectedItem);
           d.show(root);
        });
        edit.setOnAction(e->{
            UserDialog d=new UserDialog();
            d.setModel(selectedItem);
            d.isEditPurpose(true);
            d.setListener(UsersController.this);
            d.show(root);
        });
        ContextMenu menu=new ContextMenu();
        menu.getItems().addAll(edit,del);
        Window window = this.getScene().getWindow();
        menu.show(window,x,y);
    }

    private void viewUser(User selectedItem) {
        UserDialog d=new UserDialog();
        d.setModel(selectedItem);
        d.applyModel();
        d.isViewPurpose(true);
        d.show(root);
    }

    private void bulkDeleteUser(List<User> selectedItem) {
        //TODO::delete users
    }
}
