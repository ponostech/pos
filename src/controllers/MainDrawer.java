/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import com.jfoenix.controls.JFXDrawer;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

/**
 *
 * @author Sawmtea
 */
public class MainDrawer extends JFXDrawer{

    public MainDrawer() {
        this.setSidePane(new StackPane(new Button("menu1")));
        this.setDefaultDrawerSize(250);
        this.setOverLayVisible(false);
        this.setResizableOnDrag(true);
    }
    
    
    
}
