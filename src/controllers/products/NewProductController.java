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
import com.jfoenix.controls.JFXPopup;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import ponospos.entities.Category;
import ponospos.entities.Product;
import ponospos.entities.Supplier;
import ponospos.entities.Variant;
import ponospos.entities.VariantValue;
import singletons.Auth;
import singletons.PonosExecutor;
import tasks.variants.CreateVariantTask;
import util.controls.PonosTextfield;
import util.controls.VariantBox;

/**
 * FXML Controller class
 *
 * @author Sawmtea
 */
public class NewProductController extends JFXDialog implements VariantBox.VariantBoxListener {

    

    public interface ProductListener{
        public void onCreate(Product p);
    }
    @FXML
    private JFXButton newVariantBtn;
    
    @FXML 
    private VBox variantContainer;
    @FXML 
    private JFXButton addBtn;
    
    private PonosTextfield ponosTextField;
    @FXML
    private JFXComboBox<Category> categoryCombo;
    @FXML
    private JFXTextField nameField;
    @FXML
    private JFXTextArea descriptionField;
    @FXML
    private JFXComboBox<Supplier> supplierField;
    @FXML
    private JFXTextField costPriceField;
    @FXML
    private JFXTextField sellingPriceField;
    @FXML
    private JFXCheckBox taxIncludeCheck;
    @FXML
    private JFXTextField taxField;
    @FXML
    private JFXToggleButton activeToggle;
    @FXML
    private TableView<VariantValue> variantTable;
    @FXML
    private TableColumn<VariantValue,String> nameCol;
    @FXML
    private TableColumn<VariantValue,Integer> countCol;
    @FXML
    private TableColumn<VariantValue,VariantValue> actionCol;
    @FXML
    private Label title;
    @FXML
    private MaterialDesignIconView closeBtn;
    @FXML
    private ProductListener listener;
    
    private ObservableList<VariantValue> variantValues=FXCollections.observableArrayList();
    private ObservableList<Variant> variants=FXCollections.observableArrayList();
    private List<Variant> selectedVariants=FXCollections.observableArrayList();
    private ObservableList<Category> categories = FXCollections.observableArrayList();
    private ObservableList<Supplier> suppliers = FXCollections.observableArrayList();

    private boolean isCreate;
    public NewProductController(ProductListener listener){
         this.listener=listener;
        try {
            FXMLLoader loader=new FXMLLoader();
            loader.setLocation(this.getClass().getResource("/views/products/new_product.fxml"));
            loader.setController(this);
            Region region = loader.load();
            this.setContent(region);
            
            this.variantTable.setItems(variantValues);
            nameCol.setCellValueFactory(e->new SimpleStringProperty(e.getValue().getValue()));
            actionCol.setCellFactory(t->new TableCell<VariantValue,VariantValue>(){
                private Button btn;
                @Override
                protected void updateItem(VariantValue item, boolean empty) {
                    super.updateItem(item, empty); 
                    if (empty) {
                        setText(null);
                        setGraphic(null);
                    }else{
                        VariantValue value = variantValues.get(getIndex());
                        btn=new Button("",new FontAwesomeIconView(FontAwesomeIcon.TRASH));
                        btn.setOnAction(e->{
                            variantValues.remove(value);
                        });
                        setGraphic(btn);
                    }
                }
                
            });
            variantContainer.getChildren().add(new VariantBox(this));
            newVariantBtn.setOnAction(e->{
                JFXPopup popup=new JFXPopup();
                
                VBox vb=new VBox(5);
                vb.setPadding(new Insets(5));
                
                FontAwesomeIconView icon=new FontAwesomeIconView(FontAwesomeIcon.PLUS);
                icon.setFill(Color.CORAL);
                
                JFXButton btn=new JFXButton("Create",icon);
                
                Label label=new Label("Create Variant");
                label.setFont(Font.font(14));
                
                
                TextField textField=new TextField();
                textField.setPromptText("Enter the name of variant");
                
                btn.disableProperty().bind(textField.textProperty().isEmpty());
                
                btn.setOnAction(ev->{
                    Variant v=new Variant();
                    v.setName(textField.getText().trim());
                    CreateVariantTask task=new CreateVariantTask();
                    task.setVariant(v);
                    task.setOnSucceeded(event->{
                        variants.add(task.getValue());
                    });
                    PonosExecutor.getInstance().getExecutor().submit(task);
                    variants.add(v);
                    popup.hide();
                        });
                
                vb.getChildren().addAll(label,textField,btn);
                popup.setPopupContent(vb);
                popup.show(newVariantBtn, JFXPopup.PopupVPosition.TOP, JFXPopup.PopupHPosition.RIGHT);
            });
            addBtn.setOnAction(ev->{
                VariantBox box=new VariantBox(this);
                box.setVariants(variants);
                variantContainer.getChildren().add(box);
            });
            
            categoryCombo.setItems(categories);
            supplierField.setItems(suppliers);
            
            
            closeBtn.setOnMouseClicked(e->NewProductController.this.close());
        } catch (IOException ex) {
            Logger.getLogger(NewProductController.class.getName()).log(Level.SEVERE, null, ex);
        }
            
    }

    public void isCreate(boolean val){
        this.isCreate=val;
    }
    @Override
    public void add(Variant v) {
        boolean exist=false;
        for (Variant var : variants) {
            if (var.getName().equalsIgnoreCase(v.getName())) {
                exist=true;
            }
        }
        if (exist==false) {
            variants.add(v);
        }
    }

    @Override
    public void remove(VariantBox vb) {
        variantContainer.getChildren().remove(vb);
    }

    @Override
    public void separateByComma(Variant v,String text) {
         VariantValue val=new VariantValue();
         val.setValue(text);
         val.setVariant(v);
         
         variantValues.add(val);
         v.setValues(variantValues);
    }

    void setVariants(ObservableList<Variant> variants) {
        this.variants.clear();
        this.variants.addAll(variants);
    }
    
    @FXML 
    public void onPositiveButtonClick(ActionEvent e){
        if (isCreate) {
            Product p=new Product();
            p.setName(nameField.getText());
            p.setCategory(categoryCombo.getSelectionModel().getSelectedItem());
            p.setDescription(descriptionField.getText().trim());
            p.setSupplier(supplierField.getSelectionModel().getSelectedItem());
            p.setCostPrice(new BigDecimal(costPriceField.getText().trim()));
            p.setSellingPrice(new BigDecimal(sellingPriceField.getText().trim()));
            p.setIncludeTax(taxIncludeCheck.isSelected());
            p.setTax(new BigDecimal(taxField.getText().trim()));
            p.setActive(activeToggle.isSelected());
            p.setAddedBy(Auth.getInstance().getUser());
            
            p.setVariants(selectedVariants);
           
            listener.onCreate(p);
            this.close();
        }
    }
    public void setCategories(List<Category> categories){
        this.categories.clear();
        this.categories.addAll(categories);
    }
    
    void setSuppliers(ObservableList<Supplier> suppliers) {
        this.suppliers.clear();
        this.suppliers.addAll(suppliers);
    }
    
}
