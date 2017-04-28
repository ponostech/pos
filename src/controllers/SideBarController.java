/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import controllers.customers.CustomersController;
import controllers.subcontrollers.UserMenuController;
import controllers.users.UsersController;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import org.controlsfx.control.PopOver;

/**
 *
 * @author Sawmtea
 */
public class SideBarController extends Pane {
    public interface SideBarListener{
        public void onUserMenuClick();
        public void onCustomerMenuClick();
        public void onDashboardMenuClick();
    }
    @FXML
    ToggleButton userMenu;   
    @FXML
    ToggleButton customerMenu;
    @FXML
    private ToggleButton dashboardMenu;
    @FXML
    private ToggleGroup menuGroup;
    private DashboardController dashboard;
    private BorderPane container;
    private UserMenuController userMenuController;
    private UsersController userController;
    private CustomersController customerController;
    private SideBarListener listener;
    public SideBarController(){
        try {
            FXMLLoader loader=new FXMLLoader();
            loader.setLocation(this.getClass().getResource("/views/side_bar.fxml"));
            loader.setController(this);
            Parent parent = loader.load();
            this.getChildren().add(parent);
        } catch (IOException ex) {
            Logger.getLogger(SideBarController.class.getName()).log(Level.SEVERE, null, ex);
        }
       
    }
    @FXML
    public void onUserMenuClick(ActionEvent e){
        listener.onUserMenuClick();
    }
    @FXML
    private void onDashBoardMenuClick(ActionEvent event) {
       listener.onDashboardMenuClick();
    }
    @FXML
    public void onClickCustomerMenu(ActionEvent event){
        listener.onCustomerMenuClick();
    }
   
    void setDashboard(DashboardController dashboard) {
        this.dashboard=dashboard;
    }
    
    public void setListener(SideBarListener listener){
        this.listener=listener;
    }
    

}
