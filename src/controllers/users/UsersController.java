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
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
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
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Window;
import javax.persistence.RollbackException;
import org.controlsfx.control.MaskerPane;
import org.controlsfx.control.Notifications;
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
        e.consume();
        
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
        firstnameCol.setCellValueFactory(e->{
            return new SimpleStringProperty(e.getValue().getFirstName() +" "+e.getValue().getLastName());
                });
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
            FontAwesomeIconView editIcon=new FontAwesomeIconView(FontAwesomeIcon.EDIT);
            FontAwesomeIconView delIcon=new FontAwesomeIconView(FontAwesomeIcon.TRASH);
            
            Button editBtn=new Button("Edit",editIcon);
            Button delBtn=new Button("Delete",delIcon);
            
            @Override
            protected void updateItem(User item, boolean empty) {
                if (empty) {
                    setText(null);
                    setGraphic(null);
                }else{
                    editIcon.setFill(Color.BLUE);
                    delIcon.setFill(Color.BLUE);
                    editBtn.getStyleClass().add("btn-small");
                    editBtn.setOnAction(e->{
                        UserDialog dialog = new UserDialog();
                        dialog.isEditPurpose(true);
                        dialog.setTitle("Update user");
                        dialog.setModel(users.get(getIndex()));
                        dialog.setListener(UsersController.this);
                        dialog.show(root);
                        e.consume();
                        dialog.requestFocus();
                        
                    });
                    User user = users.get(getIndex());
                    if (user.getRole()==Role.ADMIN.ordinal()) {
                        delBtn.setDisable(true);
                    }
                    delBtn.getStyleClass().add("btn-small");
                    delBtn.setOnAction(e->{
                        ConfirmDialog dialog=new ConfirmDialog(ConfirmationMessage.TITLE,ConfirmationMessage.MESSAGE);
                        dialog.setListener(UsersController.this);
                        dialog.setModel(users.get(getIndex()));
                        dialog.show(root);
                        e.consume();
                        dialog.requestFocus();
                    });
                    setGraphic(new HBox(5,editBtn,delBtn));
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
                List<User> selectedItems=usersTable.getSelectionModel().getSelectedItems();
                
                if ( e.getButton()==MouseButton.SECONDARY) {
                    displayPopOver(e.getScreenX(),e.getScreenY(),selectedItems);
                }else if(e.getClickCount()==2){
                    User user = users.get(row.getIndex());
                    viewUser(user);
                }else{
                    //do nothing
                }
            });
            return row;
        });
//        newUserBtn.setOnKeyPressed(e->{
//            if (e.getCode()==KeyCode.TAB) {
//                
//            }
//            e.consume();
//        });
       
        
    }

    private void displayPopOver(double x,double y,List<User> selectedItems){
        FontAwesomeIconView editIcon=new FontAwesomeIconView(FontAwesomeIcon.EDIT);
        FontAwesomeIconView delIcon=new FontAwesomeIconView(FontAwesomeIcon.TRASH);
        FontAwesomeIconView viewIcon=new FontAwesomeIconView(FontAwesomeIcon.EYE);
        
        viewIcon.setFill(Color.CORAL);
        editIcon.setFill(Color.CORAL);
        delIcon.setFill(Color.CORAL);
                
        MenuItem view=new MenuItem("View",viewIcon);
        MenuItem edit=new MenuItem("Edit",editIcon);
        MenuItem del=new MenuItem("Delete",delIcon);
        
        ContextMenu menu=new ContextMenu();
        
        if (selectedItems.size()>1) {
            menu.getItems().add(del);
        }else{
            menu.getItems().addAll(view,edit,del);
        }
        
        view.setOnAction(e->viewUser(selectedItems.get(0)));
        del.setOnAction(e->{
            for (User selectedItem : selectedItems) {
                if (selectedItem.getRole()!=Role.ADMIN.ordinal()) {
                    ConfirmDialog d=new ConfirmDialog(ConfirmationMessage.TITLE,ConfirmationMessage.MESSAGE+" "
                        + "" +selectedItem.getUsername());
                    d.setModel(selectedItem);
                    d.setListener(UsersController.this);
                    d.show(root);
                }
            }
        });
        edit.setOnAction(e->{
            UserDialog d=new UserDialog();
            d.setModel(selectedItems.get(0));
            d.isEditPurpose(true);
            d.setListener(UsersController.this);
            d.show(root);
        });
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

    
    @Override
    public void controlFocus() {
        newUserBtn.requestFocus();
    }
}
