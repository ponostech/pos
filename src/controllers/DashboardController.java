/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import org.controlsfx.control.MaskerPane;

/**
 *
 * @author Sawmtea
 */
class DashboardController extends AnchorPane{
    

    private StackPane root;
    private MaskerPane mask;
    public DashboardController(MaskerPane mask,StackPane root) {
        this.root=root;
        this.mask=mask;
        try {
            this.mask=mask;
            FXMLLoader loader=new FXMLLoader();
            loader.setLocation(this.getClass().getResource("/views/dashboard.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            Parent parent = loader.load();
        } catch (IOException ex) {
            Logger.getLogger(SideBarController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    
}
