/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers.products;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import ponospos.entities.Product;
import ponospos.entities.Stock;
import ponospos.entities.Stores;
import singletons.Auth;
import util.TransactionType;

/**
 *
 * @author Sawmtea
 */
public class AddStockDialog extends JFXDialog {

    private void clearIfInParseable(JFXTextField qtyField) {
            try{
                Integer.parseInt(qtyField.getText());
            }catch(NumberFormatException e){
                qtyField.clear();
            }
    }

    void setProduct(Product get) {
           this.product=get;
    }

    void setStores(ObservableList<Stores> stores) {
        this.stores.clear();
        this.stores.addAll(stores);
    }
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
    private JFXComboBox<Stores> storeCombo;
    @FXML
    private JFXButton positiveBtn;
    @FXML
    private JFXButton negativeBtn;
    
    private Stock stock;
    private StockListener listener;
    private ObservableList<Stores> stores=FXCollections.observableArrayList();

    private Product product; 
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
            positiveBtn.disableProperty().bind(qtyField.textProperty().isEmpty());
            
            storeCombo.setItems(stores);
            
            hookUpEvent();
            validate();
        } catch (IOException ex) {
            Logger.getLogger(AddStockDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    public void setStock(Stock stock){
        this.stock=stock;
    }
   
    private void hookUpEvent(){
        this.qtyField.textProperty().addListener(e->clearIfInParseable(qtyField));
        positiveBtn.setOnAction(e->{
            int qty=Integer.parseInt(qtyField.getText().trim());
            
            Stock stock=new Stock();
            stock.setProduct(product);
            stock.setQuantity(qty);
            stock.setUser(Auth.getInstance().getUser());
            stock.setStore(storeCombo.getSelectionModel().getSelectedItem());
            stock.setInvoice(null);
            stock.setTransactionType(TransactionType.STOCK_UPDATE.toString());
            stock.setCreatedAt(new Date(System.currentTimeMillis()));
            stock.setRemark(remarkField.getText().trim());
            product.getStocks().add(stock);
            AddStockDialog.this.close();
            listener.onUpdate(stock);
        });
        negativeBtn.setOnAction(e->{});
    }
   
    private void validate(){
         RequiredFieldValidator required=new RequiredFieldValidator();
         MaterialDesignIconView icon=new MaterialDesignIconView(MaterialDesignIcon.STAR);
         icon.setFill(Color.RED);
         required.setIcon(icon);
         required.setMessage("Input Required");
         this.qtyField.setValidators(required);
         this.qtyField.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
             if (!newValue) {
                 qtyField.validate();
             }
         });
        
         
     }
    
    
    
    
    
}
