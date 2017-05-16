/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author Sawmtea
 */
public class ProductDialog implements Initializable {

    @FXML
    private VBox container;
    @FXML
    private JFXTextField nameField;
    @FXML
    private JFXTextField barcodeField;
    @FXML
    private JFXTextField costPriceField;
    @FXML
    private JFXTextField sellingPriceField;
    @FXML
    private JFXTextArea descriptionField;
    @FXML
    private JFXToggleButton activateToggle;
    @FXML
    private Label title;
    @FXML
    private MaterialDesignIconView close;
    @FXML
    private JFXButton positiveBtn;
    @FXML
    private JFXButton negativeBtn;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void onPositiveBtnClick(ActionEvent event) {
    }

    @FXML
    private void onNegativeBtnClick(ActionEvent event) {
    }
    
}
