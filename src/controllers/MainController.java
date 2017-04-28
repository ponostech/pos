/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import controllers.customers.CustomersController;
import controllers.users.UsersController;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import org.controlsfx.control.MaskerPane;
import ponospos.entities.User;
import singletons.Auth;

/**
 * FXML Controller class
 *
 * @author Sawmtea
 */
public class MainController extends StackPane implements SideBarController.SideBarListener{
    @FXML
    ImageView imageView;
    
    @FXML
    Label label;
    
    BorderPane layoutContainer;
    MaskerPane mask;
    
    private DashboardController dashboard;
    private SideBarController sideBar;
    private UsersController userController;
    private CustomersController customerController;

    public MainController() {
        super();
        try {
            FXMLLoader loader=new FXMLLoader();
            loader.setLocation(this.getClass().getResource("/views/main_screen.fxml"));
            loader.setController(this);
            layoutContainer= (BorderPane)loader.load();
            
            mask=new MaskerPane();
            mask.setText("Please Wait ... ");
            mask.setVisible(false);
            this.getChildren().addAll(mask,layoutContainer);
            
            initDependencies();
//            topLabel.setText(Auth.getInstance().getUser().getUsername());
//            imageView.setImage(new Image(this.getClass().getResourceAsStream("icons/ktp.png")));
        } catch (IOException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    private void initDependencies(){
        this.sideBar=new SideBarController();
        this.sideBar.setListener(this);        
        this.dashboard=new DashboardController(mask);
        this.userController=new UsersController(mask);
        this.customerController=new CustomersController(mask);
        this.layoutContainer.setLeft(sideBar);
    }

    public void hookUpEvent(User user) {
        this.label.setText("welcome "+Auth.getInstance().getUser().getUsername());
    }

    @Override
    public void onUserMenuClick() {
        switchScreen(userController);
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
   

       
    
}
