/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers.sales;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.Region;
import ponospos.entities.Product;

/**
 *
 * @author Sawmtea
 */
public class QuantityDialog extends JFXDialog {
    public interface QuantityListener{
        public void onQuantitySet(Product product,int qty);
    }
    
    @FXML
    private Spinner quantitySpinner;
    @FXML
    private JFXButton positiveBtn;
    
    private Product product;
    private int quantity;
    private QuantityListener listener;
    
    public QuantityDialog(QuantityListener listener){
        try {
            this.listener=listener;
            FXMLLoader loader=new FXMLLoader();
            loader.setLocation(this.getClass().getResource("/views/sales/quantity_dialog.fxml"));
            loader.setController(this);
            Region parent = loader.load();
            this.setContent(parent);
           
            positiveBtn.setOnAction(e->{
                int qty=(Integer)quantitySpinner.getValue();
                close();
                listener.onQuantitySet(product, qty);
            });
            this.setOnDialogOpened(e->quantitySpinner.requestFocus());
        } catch (IOException ex) {
            Logger.getLogger(QuantityDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void setMaxValue(int max){
        SpinnerValueFactory sp=new SpinnerValueFactory.IntegerSpinnerValueFactory(1, max);
        quantitySpinner.setValueFactory(sp);
    }

    public void setProduct(Product product) {
        this.product = product;
    }
    
}
