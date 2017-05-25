/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers.products;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;
import com.jfoenix.validation.RequiredFieldValidator;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import ponospos.entities.Attribute;
import ponospos.entities.Category;
import ponospos.entities.Product;
import ponospos.entities.Stock;
import ponospos.entities.Supplier;
import singletons.Auth;
import util.TransactionType;
import util.controls.VariantControl;

/**
 * FXML Controller class
 *
 * @author Sawmtea
 */
public class ProductDialog extends JFXDialog {

    public interface ProductDialogListener{
        public void onCreate(Product product);
        public void onUpdate(Product product);
    }

    @FXML
    private VBox container;
    @FXML
    private JFXComboBox<Category>categoryCombo;
    @FXML
    private JFXComboBox<Supplier>supplierCombo;
    @FXML
    private JFXTextField nameField;
    @FXML
    private JFXTextField barcodeField;
    @FXML
    private JFXTextField costPriceField;
    @FXML
    private JFXTextField sellingPriceField;
    @FXML
    private JFXCheckBox taxIncludeCheck;
    @FXML
    private JFXTextArea descriptionField;
    @FXML
    private JFXToggleButton activateToggle;
    @FXML
    private JFXTextField taxField;
    @FXML
    private Label title;
    @FXML
    private MaterialDesignIconView close;
    @FXML
    private JFXButton positiveBtn;
    @FXML
    private JFXButton negativeBtn;
    @FXML
    private JFXTextField qtyField;
    @FXML
    private JFXTextArea remarkField;
    
    @FXML
    private TextField variantNameField;
    @FXML
    private TextField variantValueField;
    @FXML
    private Button variantAddBtn;
   
    private Product product;
    private boolean isView;
    private boolean isCreate;
    private boolean isEdit;
    private ProductDialogListener listener;
    
    private VariantControl variantControl;
    
    private ObservableList categories=FXCollections.observableArrayList();
    private ObservableList suppliers=FXCollections.observableArrayList();
    public ProductDialog(ProductDialogListener listener){
        try {
            this.variantControl=new VariantControl();
            this.listener=listener;
            FXMLLoader loader=new FXMLLoader();
            loader.setLocation(this.getClass().getResource("/views/products/product_dialog.fxml"));
            loader.setController(this);
            Region region = loader.load();
            this.setContent(region);
            this.setOnDialogOpened(e->categoryCombo.requestFocus());
            
            this.container.getChildren().add(variantControl);
            
            this.categoryCombo.setItems(categories);
            this.supplierCombo.setItems(suppliers);
            
            this.close.setOnMouseClicked(e->ProductDialog.this.close());
            
            this.sellingPriceField.setOnKeyReleased(e->{
                clearIfTextInparseable(sellingPriceField);
            });
            this.costPriceField.setOnKeyReleased(e->{
                clearIfTextInparseable(costPriceField);
            });
           
            positiveBtn.disableProperty().bind(nameField.textProperty().isEmpty()
            .or(sellingPriceField.textProperty().isEmpty()
            )
            );
            
            taxIncludeCheck.selectedProperty().addListener(e->{
                if (taxIncludeCheck.isSelected()) {
                    taxField.setText("0");
                }
            });
            
            variantAddBtn.setOnAction(e->{
                if (variantNameField.getText().isEmpty() || variantValueField.getText().isEmpty()) {
                    return;
                }
                Attribute attr=new Attribute();
                attr.setName(variantNameField.getText().trim());
                attr.setValue(variantValueField.getText().trim());
                
                variantControl.addAttribute(attr);
                
                variantNameField.clear();
                variantValueField.clear();
            });
            validate();
        } catch (IOException ex) {
            Logger.getLogger(ProductDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public ProductDialog isView(){
        this.isView=true;
        
        return this;
    }
    public ProductDialog isCreate(){
        this.isCreate=true;
        
        return this;
    }
    public ProductDialog isEdit(){
        this.isEdit=true;
        if (product.getCategory()!=null) {
            categoryCombo.getSelectionModel().select(product.getCategory());
        }
        if (product.getSupplier()!=null) {
            supplierCombo.getSelectionModel().select(product.getSupplier());
        }
        nameField.setText(product.getName());
        barcodeField.setText(product.getBarcode());
        descriptionField.setText(product.getDescription());
        activateToggle.setSelected(product.isActive());
        costPriceField.setText(Double.toString(product.getCostPrice().doubleValue()));
        sellingPriceField.setText(Double.toString(product.getSellingPrice().doubleValue()));
        taxIncludeCheck.setSelected(product.isIncludeTax());
        taxField.setText(Double.toString(product.getTax().doubleValue()));
       
        for (Attribute at : product.getAttributes()) {
            variantControl.addAttribute(at);
        }
        this.positiveBtn.setText("Update");
        this.title.setText("Edit product");
        qtyField.setText("");
        qtyField.setDisable(true);
        return this;
    }
    
    public void setCategories(ObservableList<Category> cat){
        this.categories.clear();
        this.categories.addAll(cat);
    }
    public void setProduct(Product product){
        this.product=product;
    }
    
    
   @FXML
    private void onPositiveBtnClick(ActionEvent event) {
        if (isCreate) {
            Stock stock=new Stock();
            
            Product p=new Product();
           p.setName(nameField.getText().trim());
           p.setBarcode(barcodeField.getText().trim());
           p.setDescription(descriptionField.getText().trim());
            if (categoryCombo.getSelectionModel().getSelectedItem()!=null) {
                p.setCategory(categoryCombo.getSelectionModel().getSelectedItem());
            }
            if (supplierCombo.getSelectionModel().getSelectedItem()!=null) {
                p.setSupplier(supplierCombo.getSelectionModel().getSelectedItem());
            }
            if (costPriceField.getText().isEmpty()) {
                p.setCostPrice(BigDecimal.ZERO);
            }else{
                p.setCostPrice(new BigDecimal(costPriceField.getText().trim()));
            }
            p.setSellingPrice(new BigDecimal(sellingPriceField.getText().trim()));
            p.setCreatedAt(new Date(System.currentTimeMillis()));
            p.setIncludeTax(taxIncludeCheck.isSelected());
            p.setActive(activateToggle.isSelected());
            p.setAddedBy(Auth.getInstance().getUser());
            p.setTax(new BigDecimal(taxField.getText().trim()));
            for (Attribute att : variantControl.getAttributes()) {
                att.setProduct(p);
            }
            p.setAttributes(variantControl.getAttributes());

            
            /*
                Stock implementataion
            */
            stock.setProduct(p);
            stock.setQuantity(Integer.parseInt(qtyField.getText()));
            stock.setUpdateAt(new Date(System.currentTimeMillis()));
            stock.setStore(Auth.getInstance().getStore());
            stock.setRemark(remarkField.getText().trim());
            stock.setTransactionType(TransactionType.STOCK_UPDATE.toString());
            stock.setUser(Auth.getInstance().getUser());
            stock.setInvoice(null);
            List<Stock> stocks=new ArrayList();
            stocks.add(stock);
            p.setStocks(stocks);
            /* ------------------------------------ */
            /*  Stock implementation end */
            /*------------------------------------*/
            this.close();
            listener.onCreate(p);
       }else if (isEdit) {
            
           product.setName(nameField.getText().trim());
           product.setBarcode(barcodeField.getText().trim());
           product.setDescription(descriptionField.getText().trim());
            if (categoryCombo.getSelectionModel().getSelectedItem()!=null) {
                product.setCategory(categoryCombo.getSelectionModel().getSelectedItem());
            }
            if (costPriceField.getText().isEmpty()) {
                product.setCostPrice(BigDecimal.ZERO);
            }else{
                product.setCostPrice(new BigDecimal(costPriceField.getText().trim()));
            }
            product.setSellingPrice(new BigDecimal(sellingPriceField.getText().trim()));
            product.setIncludeTax(taxIncludeCheck.isSelected());
            product.setActive(activateToggle.isSelected());
            product.setEdittedBy(Auth.getInstance().getUser());
            product.setTax(new BigDecimal(taxField.getText().trim()));
            
            for (Attribute att : variantControl.getAttributes()) {
                att.setProduct(product);
            }
            product.setAttributes(variantControl.getAttributes());
            this.close();
            listener.onUpdate(product);
            
       }else{
           
       }
        
    }

    @FXML
    private void onNegativeBtnClick(ActionEvent event) {
        categories=null;
        this.close();
    }
     private void clearIfTextInparseable(JFXTextField field){
        try{
            Double.parseDouble(field.getText());
        }catch(NumberFormatException e){
            field.clear();
        }
    }
     
      void setSuppliers(ObservableList<Supplier> suppliers) {
          this.suppliers.clear();
          this.suppliers.addAll(suppliers);
    }
      
     private void validate(){
         RequiredFieldValidator required=new RequiredFieldValidator();
         MaterialDesignIconView icon=new MaterialDesignIconView(MaterialDesignIcon.STAR);
         icon.setFill(Color.RED);
         required.setIcon(icon);
         required.setMessage("Input Required");
         this.nameField.setValidators(required);
         this.nameField.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
             if (!newValue) {
                 nameField.validate();
             }
         });
         this.sellingPriceField.setValidators(required);
         this.sellingPriceField.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
             if (!newValue) {
                 sellingPriceField.validate();
             }
         });
         this.taxField.setValidators(required);
         this.taxField.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
             if (!newValue) {
                 taxField.validate();
             }
         });
         
     }
          
          
      
    
}
