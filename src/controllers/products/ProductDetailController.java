/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers.products;

import com.jfoenix.controls.JFXDialog;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import ponospos.entities.Category;
import ponospos.entities.Product;
import ponospos.entities.Supplier;

/**
 * FXML Controller class
 *
 * @author Sawmtea
 */
public class ProductDetailController extends JFXDialog  {

    @FXML
    private Button closeBtn;
    @FXML
    private Label idLabel;
    @FXML
    private Label nameLabel;
    @FXML
    private Label barLabel;
    @FXML
    private Label costPricelabel;
    @FXML
    private Label sellingPriceLabel;
    @FXML
    private Label taxLabel;
    @FXML
    private Label categoryLabel;
    @FXML
    private Label supplierLabel;
    @FXML
    private Label descriptionLabel;
    @FXML
    private Label userLabel;
    @FXML
    private Label dateLabel;

    public ProductDetailController() {
        super();
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(this.getClass().getResource("/views/products/product_detail.fxml"));
            loader.setController(this);
            Region region = loader.load();
            this.setContent(region);
            this.setOnDialogOpened(e->closeBtn.requestFocus());
        } catch (IOException ex) {
            Logger.getLogger(ProductDetailController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @FXML
    public void onCloseButtonClick(ActionEvent e){
        this.close();
    }
    
    public ProductDetailController populate(Product product){
        this.idLabel.setText("Id : "+product.getId());
        this.nameLabel.setText("Name : "+product.getName());
        this.barLabel.setText("Barcode : "+product.getBarcode());
        this.costPricelabel.setText("Cost Price : "
                +NumberFormat.getCurrencyInstance(new Locale("en","in")).format(product.getCostPrice().doubleValue()));
        this.sellingPriceLabel.setText("Selling Price : "
                +NumberFormat.getCurrencyInstance(new Locale("en","in")).format(product.getSellingPrice().doubleValue()));
        this.taxLabel.setText("Selling Price : "
                +NumberFormat.getCurrencyInstance(new Locale("en","in")).format(product.getTax().doubleValue()));
        Category c=product.getCategory();
        if (c!=null) {
            this.categoryLabel.setText("Category : " + product.getCategory().getName());
        }else{
            this.categoryLabel.setText("Category : " + "NA");
        }
        Supplier s = product.getSupplier();
        if (s != null) {
            this.categoryLabel.setText("Supplier : " + product.getSupplier().getName());
        } else {
            this.categoryLabel.setText("Supplier : " + "NA");
        }
        this.descriptionLabel.setText("Description : "+product.getDescription());
        this.userLabel.setText("Added by : "+product.getAddedBy().getUsername());
        this.dateLabel.setText("Added At : "+new SimpleDateFormat("EEE dd MMM YYY").format(product.getCreatedAt()));
        return this;
    }
    

      
    
}
