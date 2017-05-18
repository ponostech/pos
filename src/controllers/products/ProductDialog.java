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
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import ponospos.entities.Attribute;
import ponospos.entities.Category;
import ponospos.entities.Product;
import ponospos.entities.Variation;
import singletons.Auth;

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
    private Label title;
    @FXML
    private MaterialDesignIconView close;
    @FXML
    private JFXButton positiveBtn;
    @FXML
    private JFXButton negativeBtn;

   
    private Product product;
    private boolean isView;
    private boolean isCreate;
    private boolean isEdit;
    private ProductDialogListener listener;
    private ObservableList categories=FXCollections.observableArrayList();
    private List<Attribute> attributes=new ArrayList<>();
    public ProductDialog(ProductDialogListener listener){
        try {
            this.listener=listener;
            FXMLLoader loader=new FXMLLoader();
            loader.setLocation(this.getClass().getResource("/views/products/product_dialog.fxml"));
            loader.setController(this);
            Region region = loader.load();
            this.setContent(region);
            this.setOnDialogOpened(e->categoryCombo.requestFocus());
            this.categoryCombo.setItems(categories);
            this.categoryCombo.setOnAction(e->{
                Category cat=categoryCombo.getSelectionModel().getSelectedItem();
                container.getChildren().clear();
                generateControlsByVariation(cat.getVariations());
            });
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
        nameField.setText(product.getName());
        barcodeField.setText(product.getBarcode());
        descriptionField.setText(product.getDescription());
        activateToggle.setSelected(product.getIsActive());
        costPriceField.setText(Double.toString(product.getCostPrice().doubleValue()));
        sellingPriceField.setText(Double.toString(product.getSellingPrice().doubleValue()));
        taxIncludeCheck.setSelected(product.isIncludeTax());
        
       
        if (product.getAttributes()!=null
                && product.getCategory()!=null
                && !product.getCategory().getVariations().isEmpty()) {
            List<Attribute> attrs = product.getAttributes();
            List<Variation> variations = product.getCategory().getVariations();
            
            generateControlsByAttributes(product);
        }
        
        this.positiveBtn.setText("Update");
        this.title.setText("Edit product");
        return this;
    }
    
    public void setCategories(ObservableList<Category> cat){
        this.categories.clear();
        this.categories.addAll(cat);
    }
    public void setProduct(Product product){
        this.product=product;
    }
    
     private void generateControlsByAttributes(Product p) {
         container.getChildren().clear();
        List<Variation> variations = p.getCategory().getVariations();
        attributes = p.getAttributes();
        Set<String> distinctName=getDistinctVariationName(variations);
         for (String name : distinctName) {
             
             ObservableList<String> items=FXCollections.observableArrayList();
            JFXComboBox <String> optionsBox=new JFXComboBox(items);
            optionsBox.setPromptText(name);
            optionsBox.setPrefSize(350, USE_PREF_SIZE);
            optionsBox.setPadding(new Insets(5));
            optionsBox.setLabelFloat(true);
            VBox.setVgrow(optionsBox, Priority.ALWAYS);

            optionsBox.setOnAction(e->{
                for (Attribute attr : attributes) {
                    String selectedItem = optionsBox.getSelectionModel().getSelectedItem();
                    if (attr.getName().equalsIgnoreCase(name)) {
                        attr.setValue(selectedItem);
                        
                    }
                }
            });
            
            for(String val:extractVariationValuesByName(variations,name)){
                items.add(val);
            }
             for (Attribute attr:attributes) {
                 if (attr.getName().equalsIgnoreCase(name)) {
                     optionsBox.getSelectionModel().select(attr.getValue());
                 }
             }
             container.getChildren().add(optionsBox);
             
         }
     }
     
    
    private void generateControlsByVariation(List<Variation> var){
        
        Set<String> names=getDistinctVariationName(var);
        
        for (String name : names) {
            ObservableList<String> items=FXCollections.observableArrayList();
            JFXComboBox <String> optionsBox=new JFXComboBox(items);
            optionsBox.setPromptText(name);
            optionsBox.setPrefSize(350, USE_PREF_SIZE);
            optionsBox.setPadding(new Insets(5));
            
            optionsBox.setLabelFloat(true);
            VBox.setVgrow(optionsBox, Priority.ALWAYS);
            optionsBox.setOnAction(e->{
                Attribute attr=new Attribute();
                attr.setName(name);
                attr.setValue(optionsBox.getSelectionModel().getSelectedItem());
                attributes.add(attr);
            });
            for(String val:extractVariationValuesByName(var,name)){
                items.add(val);
            }
           
            container.getChildren().add(optionsBox);
        }
    }
    private Iterable<String> extractVariationValuesByName(List<Variation> var, String name) {
        ArrayList<String> values=new ArrayList<String>();
        for (Variation variation : var) {
                if (variation.getName().equalsIgnoreCase(name)) {
                    values.add(variation.getValue());
                }
        }
        return values;
    }
    
    private Set<String> getDistinctVariationName(List<Variation> var) {
        Set<String> names=new HashSet<>();
        var.forEach((v) -> {
            names.add(v.getName());
        });
        return names;
    }
    
   @FXML
    private void onPositiveBtnClick(ActionEvent event) {
        if (isCreate) {
           Product p=new Product();
           p.setName(nameField.getText().trim());
           p.setBarcode(barcodeField.getText().trim());
           p.setDescription(descriptionField.getText().trim());
            if (categoryCombo.getSelectionModel().getSelectedItem()!=null) {
                p.setCategory(categoryCombo.getSelectionModel().getSelectedItem());
            }
            if (costPriceField.getText().isEmpty()) {
                p.setCostPrice(BigDecimal.ZERO);
            }else{
                p.setCostPrice(new BigDecimal(costPriceField.getText().trim()));
            }
            p.setSellingPrice(new BigDecimal(sellingPriceField.getText().trim()));
            p.setCreatedAt(new Date(System.currentTimeMillis()));
            p.setIncludeTax(taxIncludeCheck.isSelected());
            p.setIsActive(activateToggle.isSelected());
            p.setAddedBy(Auth.getInstance().getUser());
            p.setAttributes(attributes);
            for(Attribute attr:attributes){
                attr.setProduct(p);
            }
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
            product.setIsActive(activateToggle.isSelected());
            product.setEdittedBy(Auth.getInstance().getUser());
            product.setAttributes(attributes);
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
    
}
