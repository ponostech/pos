/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import Messages.ProfileMessage;
import Messages.RoleMessage;
import controllers.customers.CustomersController;
import controllers.users.ProfileDialog;
import controllers.users.UsersController;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import org.controlsfx.control.MaskerPane;
import org.controlsfx.control.Notifications;
import ponospos.PonosPos;
import ponospos.entities.User;
import singletons.Auth;
import singletons.PonosExecutor;
import tasks.users.UpdateTask;
import util.Role;


/**
 * FXML Controller class
 *
 * @author Sawmtea
 */
public class MainController extends StackPane 
        implements SideBarController.SideBarListener,
        ProfileDialog.ProfileDialogListener,
        PonosControllerInterface{
    
    @FXML
    Label label;
    @FXML
    SplitMenuButton profileMenuButton;
    @FXML
    MenuItem profileMenu;
    @FXML
    MenuItem logoutMenu;
    
    BorderPane layoutContainer;
    MaskerPane mask;
    
    private DashboardController dashboard;
    private SideBarController sideBar;
    private UsersController userController;
    private CustomersController customerController;
    private ProfileDialog profileDialog;
    private PonosPos app;
    public MainController(PonosPos app) {
        super();
        this.app=app;
        try {

            FXMLLoader loader=new FXMLLoader();
            loader.setLocation(this.getClass().getResource("/views/main_screen.fxml"));
            loader.setController(this);
            layoutContainer= (BorderPane)loader.load();
            profileMenuButton.setGraphic(new Circle(20, new ImagePattern(
                    new Image(this.getClass().getResource("/resource/icons/ktp.png").toExternalForm()))));
            
            mask=new MaskerPane();
            mask.setText("Please Wait... ");
            mask.setVisible(false);
            this.getChildren().addAll(layoutContainer,mask);
            
//            topLabel.setText(Auth.getInstance().getUser().getUsername());
//            imageView.setImage(new Image(this.getClass().getResourceAsStream("icons/ktp.png")));
        } catch (IOException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    @Override
    public void initDependencies(){
        this.sideBar=new SideBarController();
        this.sideBar.setListener(this);        
        this.dashboard=new DashboardController(mask);
        this.userController=new UsersController(mask,this);
        this.customerController=new CustomersController(mask,this);
        this.layoutContainer.setLeft(sideBar);
        
        this.userController.initDependencies();
        this.customerController.initDependencies();
    }
    @Override
    public void initControls(){
        this.userController.initControls();
        this.customerController.initControls();
    }

    @Override
    public void hookupEvent() {
        
        userController.hookupEvent();
        this.customerController.hookupEvent();
    }
    @Override
    public void bindControls(){
        this.userController.bindControls();
        this.customerController.bindControls();
    
    }

    @Override
    public void onUserMenuClick() {
        User user = Auth.getInstance().getUser();
        if (user.getRole()==Role.EMPLOYEE.ordinal()) {
            Notifications.create().title(RoleMessage.PERMISSION_DENIED_TITLE).text(RoleMessage.PERMISSION_DENIED_MESSAGE).showWarning();
            return;
        }
        switchScreen(userController);
        userController.fetchAllUser();
    }

    @Override
    public void onCustomerMenuClick() {
        switchScreen(customerController);
        customerController.fetchAllCustomer();
    }

    @Override
    public void onDashboardMenuClick() {
        switchScreen(dashboard);
    }
    private void switchScreen(Parent parent){
        //TODO::animation goes here
        this.layoutContainer.setCenter(parent);
    }

    @Override
    public void onProfileChange(User user) {
    
        UpdateTask task=new UpdateTask();
        task.setUser(user);
        mask.visibleProperty().bind(task.runningProperty());
        task.setOnFailed(e->task.getException().printStackTrace());
        task.setOnSucceeded(e->{
            label.setText("hello "+Auth.getInstance().getUser().getUsername());
             Notifications.create()
                    .title(ProfileMessage.UPDATE_SUCCESS_TITLE)
                    .text(ProfileMessage.UPDATE_SUCCESS_MESSAGE)
                    .showInformation();
        });
        PonosExecutor.getInstance().getExecutor().submit(task);
        
    }
    @FXML
    public void onProfileMenuClick(){
        ProfileDialog d=new ProfileDialog(this);
        
        d.show(this);
        
        Platform.runLater(()->d.requestFocusToUsername());
      
    }
   
    @FXML
    public void onLogoutMenuClick(){
        Auth.getInstance().setIsLogged(false);
        sideBar.displayDashBoard();
        greetUser();
        app.displayLoginScreen();
    }
    
    public void greetUser(){
        label.setText(Auth.getInstance().getUser().getUsername());
    }

       
    
}
