/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers.subcontrollers;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

/**
 *
 * @author Sawmtea
 */
public class UserMenuController extends VBox {
    
    public UserMenuController() {
        try {
            FXMLLoader loader=new FXMLLoader();
            loader.setLocation(this.getClass().getResource("/views/includes/user_menu.fxml"));
            loader.setController(this);
            VBox root = loader.load();
            this.getChildren().add(root);
        } catch (IOException ex) {
            Logger.getLogger(UserMenuController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    @FXML
    public void onNewUserClick(MouseEvent e){
        
       
    }
    @FXML
    public void onUserClick(MouseEvent e){
        
    }
    
}
