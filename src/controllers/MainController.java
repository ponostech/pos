/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import Messages.ProfileMessage;
import Messages.RoleMessage;
import controllers.categories.CategoryController;
import controllers.customers.CustomersController;
import controllers.stores.StoresController;
import controllers.suppliers.SuppliersController;
import controllers.users.ProfileDialog;
import controllers.users.UsersController;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
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
    MenuButton profileMenuButton;
    
    @FXML
    MenuItem profileMenu;
    
    @FXML
    MenuItem logoutMenu;
    
    @FXML
    Circle avatarCircle;
    
    BorderPane layoutContainer;
    MaskerPane mask;
    
    
    private SuppliersController supplierController;
    private DashboardController dashboard;
    private SideBarController sideBar;
    private UsersController userController;
    private CustomersController customerController;
    private CategoryController categoryController;
    private ProfileDialog profileDialog;
    private StoresController storeController;
    private PonosPos app;
    public MainController(PonosPos app) {
        super();
        this.app=app;
        try {
            FXMLLoader loader=new FXMLLoader();
            loader.setLocation(this.getClass().getResource("/views/main_screen.fxml"));
            loader.setController(this);
            layoutContainer= (BorderPane)loader.load();
            mask=new MaskerPane();
            mask.setText("Please Wait... ");
            mask.setVisible(false);
            this.getChildren().addAll(layoutContainer,mask);
            
            this.avatarCircle.setFill(
                    new ImagePattern(
                            new Image(this.getClass().getResourceAsStream("/resource/icons/avatar.png"))
                    )
            );
            
            this.getStyleClass().add("root-container");
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
        this.supplierController=new SuppliersController(mask, this);
        this.categoryController=new CategoryController(mask,this);
        this.storeController=new StoresController(mask,this);
        this.layoutContainer.setLeft(sideBar);
        
        this.userController.initDependencies();
        this.customerController.initDependencies();
        this.supplierController.initDependencies();
        this.categoryController.initDependencies();
    }
    @Override
    public void initControls(){
        this.userController.initControls();
        this.customerController.initControls();
        this.supplierController.initControls();
        this.categoryController.initControls();
        this.storeController.initControls();
    }

    @Override
    public void hookupEvent() {
        
        userController.hookupEvent();
        this.customerController.hookupEvent();
        this.supplierController.hookupEvent();
        this.categoryController.hookupEvent();
        this.storeController.hookupEvent();
    }
    @Override
    public void bindControls(){
        this.userController.bindControls();
        this.customerController.bindControls();
        this.supplierController.bindControls();
        this.categoryController.bindControls();
        this.storeController.bindControls();
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
        userController.controlFocus();
        
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
        FadeTransition fadeOut=new FadeTransition(Duration.millis(2000));
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setNode(layoutContainer.getCenter());
        fadeOut.play();
        
        this.layoutContainer.setCenter(parent);
        FadeTransition fadeIn=new FadeTransition(Duration.seconds(2));
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.setNode(layoutContainer.getCenter());
        fadeIn.play();
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

    @Override
    public void controlFocus() {
        userController.controlFocus();
        customerController.controlFocus();
        supplierController.controlFocus();
    }

    
    @Override
    public void onCategoryMenuClick() {
           switchScreen(categoryController);
           categoryController.fetchAllCategories();
    }

    @Override
    public void onSupplierMenuClick() {
        switchScreen(supplierController);
        supplierController.fetchAllSupplier();
    }

    @Override
    public void onProductMenuClick() {
        
    }

    @Override
    public void onStoreMenuClick() {
        switchScreen(storeController);
        storeController.fetchAllStores();
    }

       
    
}
