/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import Messages.SideBarMessage;
import controllers.subcontrollers.UserMenuController;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.controlsfx.control.PopOver;

/**
 *
 * @author Sawmtea
 */
public class SideBarController extends Pane {
    @FXML
    ToggleButton userMenu;    
    @FXML
    private ToggleButton dashboardMenu;
    @FXML
    private ToggleGroup menuGroup;
    private DashboardController dashboard;
    private BorderPane container;
    private PopOver userSubMenuPopOver;
    private UserMenuController userMenuController;
    public SideBarController(){
        try {
            FXMLLoader loader=new FXMLLoader();
            loader.setLocation(this.getClass().getResource("/views/side_bar.fxml"));
            loader.setController(this);
            Parent parent = loader.load();
            this.getChildren().add(parent);
            init();
        } catch (IOException ex) {
            Logger.getLogger(SideBarController.class.getName()).log(Level.SEVERE, null, ex);
        }
       
    }
    @FXML
    public void onUserMenuClick(ActionEvent e){
        userSubMenuPopOver.show(userMenu);
    }
    @FXML
    private void onDashBoardMenuClick(ActionEvent event) {
        switchScreen(dashboard);
    }
    public void setContainer(BorderPane container){
        this.container=container;
    }
    public void switchScreen(Parent view){
        this.container.setCenter(view);
    }

    void setDashboard(DashboardController dashboard) {
        this.dashboard=dashboard;
    }
    private void init(){
        userMenuController=new UserMenuController();
         userSubMenuPopOver=new PopOver();
        userSubMenuPopOver.setContentNode(userMenuController);
    }
    

}
