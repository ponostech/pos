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
import javafx.scene.layout.Pane;
import org.controlsfx.control.MaskerPane;

/**
 *
 * @author Sawmtea
 */
class DashboardController extends Pane{
    

    private MaskerPane mask;
    public DashboardController(MaskerPane mask) {
        try {
            this.mask=mask;
            FXMLLoader loader=new FXMLLoader();
            loader.setLocation(this.getClass().getResource("/views/dashboard.fxml"));
            loader.setController(this);
            Parent parent = loader.load();
            this.getChildren().add(parent);
        } catch (IOException ex) {
            Logger.getLogger(SideBarController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    
}
