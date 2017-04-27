/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import ponospos.entities.User;
import singletons.Auth;

/**
 * FXML Controller class
 *
 * @author Sawmtea
 */
public class MainController extends StackPane {
    @FXML
    ImageView imageView;
    
    @FXML
    Label label;
    
    BorderPane layoutContainer;
    
    private DashboardController dashboard;
    private SideBarController sideBar;

 
    public MainController() {
        super();
        try {
            FXMLLoader loader=new FXMLLoader();
            loader.setLocation(this.getClass().getResource("/views/main_screen.fxml"));
            loader.setController(this);
            layoutContainer= (BorderPane)loader.load();
            this.getChildren().add(layoutContainer);
            init();
//            topLabel.setText(Auth.getInstance().getUser().getUsername());
//            imageView.setImage(new Image(this.getClass().getResourceAsStream("icons/ktp.png")));
        } catch (IOException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    private void init(){
        this.sideBar=new SideBarController();
        this.dashboard=new DashboardController();
        this.layoutContainer.setLeft(sideBar);
        //pass the container to sidebar
        this.sideBar.setContainer(layoutContainer);
        this.sideBar.setDashboard(dashboard);
        
        this.label.setText("welcome "+Auth.getInstance().getUser().getUsername());
    }

    public void setUser(User user) {
    }
   

       
    
}
