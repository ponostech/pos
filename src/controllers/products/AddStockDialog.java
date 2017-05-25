/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers.products;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import ponospos.entities.Stock;

/**
 *
 * @author Sawmtea
 */
public class AddStockDialog extends JFXDialog {
    public interface StockListener{
        public void onUpdate(Stock stock);
    }
    @FXML
    private MaterialDesignIconView close;
    @FXML
    private Label title;
    @FXML
    private JFXTextField qtyField;
    @FXML
    private JFXTextArea remarkField;
    @FXML
    private JFXButton positiveBtn;
    @FXML
    private JFXButton negativeBtn;
    
    private StockListener listener;

    public AddStockDialog(StockListener listener) {
        super();
        try {
            this.listener=listener;
            FXMLLoader loader=new FXMLLoader();
            loader.setLocation(this.getClass().getResource("/views/products/add_stock_dialog.fxml"));
            loader.setController(this);
            Region region = loader.load();
            this.setContent(region);
            this.setOnDialogOpened(e->qtyField.requestFocus());
        } catch (IOException ex) {
            Logger.getLogger(AddStockDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    
    
}
