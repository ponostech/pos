/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers.categories;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import ponospos.entities.Category;
import ponospos.entities.Variation;

/**
 *
 * @author Sawmtea
 */
public class CategoryDialog  extends JFXDialog{

   
    public interface CategoryDialogListener{
        public void onCreate(Category category);
        public void onUpdate(Category category);
    }

    @FXML
    private MaterialDesignIconView close;
    @FXML
    private Label title;
    @FXML
    private JFXTextField nameField;
    @FXML
    private JFXTextArea descriptionField;
    @FXML
    private JFXButton positiveBtn;
    @FXML
    private JFXButton negativeBtn;
    @FXML
    private VBox container;
    @FXML 
    TextField variationNameField;
    @FXML 
    TextField variationValueField;
    @FXML
    JFXButton addVariationBtn;
    
    private boolean isEdit;
    private boolean isCreate;
    private boolean isView;
    private Category category;
    private CategoryDialogListener listener;
    private List<Variation> variations=new ArrayList<Variation>();
    
    public CategoryDialog(CategoryDialogListener listener){
        try {
            this.listener=listener;
            FXMLLoader loader=new FXMLLoader();
            loader.setLocation(this.getClass().getResource("/views/categories/category_dialog.fxml"));
            loader.setController(this);
            Region region = loader.load();
            this.setContent(region);
            this.setOnDialogOpened(e->this.nameField.requestFocus());
            close.setOnMouseClicked(e->CategoryDialog.this.close());
            this.positiveBtn.disableProperty().bind(nameField.textProperty().isEmpty());
            this.addVariationBtn.disableProperty().bind(variationNameField.textProperty().isEmpty().or(variationValueField.textProperty().isEmpty()));
        } catch (IOException ex) {
            Logger.getLogger(CategoryDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @FXML
    private void onPositiveBtnClick(ActionEvent event) {
        if (isCreate) {
            Category c=new Category();
            c.setName(nameField.getText().trim());
            c.setDescription(descriptionField.getText().trim());
            c.setVariations(variations);
            for (Variation v : variations) {
                v.setCategory(c);
            }
            this.close();
            listener.onCreate(c);
        }else if (isEdit) {
            category.setName(nameField.getText().trim());
            category.setDescription(descriptionField.getText().trim());
            category.setVariations(variations);
            for (Variation v : variations) {
                v.setCategory(category);
            }
            this.close();
            listener.onUpdate(category);
        }else{
            this.close();
        }

    }
    public CategoryDialog isCreate(boolean val){
        this.isCreate=true;
        this.title.setText("Create Category");
        this.positiveBtn.setText("Create");
        return this;
    }
     public CategoryDialog isView(){
        this.isView=true;
        this.title.setText("Category Info");
        this.positiveBtn.setText("Close");
        return this;
    }
     public CategoryDialog setCategory(Category category){
         this.category=category;
         this.nameField.setText(category.getName());
         this.descriptionField.setText(category.getDescription());
         for (Variation v : category.getVariations()) {
             addNewVariation(v);
         }
         return this;
     }
    public CategoryDialog isToUpdate(Category cat){
        this.isEdit=true;
        this.positiveBtn.setText("Update");
        this.title.setText("Edit Category");
        this.category=cat;
        this.nameField.setText(cat.getName());
        this.descriptionField.setText(cat.getDescription());
        for (Variation v : category.getVariations()) {
             addNewVariation( v);
         }
        return this;
    }

    @FXML
    private void onNegativebtnClick(ActionEvent event) {
        this.close();
    }
    @FXML
    private void onAddBtnClick(ActionEvent event) {
        Variation v=new Variation();
        v.setName(variationNameField.getText().trim());
        v.setValue(variationValueField.getText().trim());
        addNewVariation(v);
        variationValueField.clear();
        event.consume();
        variationNameField.requestFocus();
    }
    
     private void addNewVariation(Variation v) {
         HBox hb=new HBox(10);
         
         Label nameLabel=new Label();
         nameLabel.setMaxSize(200, 20);
         HBox.setHgrow(nameLabel, Priority.ALWAYS);
         
         Label valueLabel=new Label();
         valueLabel.setMaxSize(200, 20);
         HBox.setHgrow(valueLabel, Priority.ALWAYS);
         
         Button delBtn=new Button("",new FontAwesomeIconView(FontAwesomeIcon.TRASH));
         
         variations.add(v);
         
         nameLabel.setText(v.getName());
         valueLabel.setText(v.getValue());
         
         delBtn.setOnAction(e->{
             variations.remove(v);
             container.getChildren().remove(hb);
         });
         hb.getChildren().addAll(nameLabel,valueLabel,delBtn);
         container.getChildren().add(hb);
         
    }
}
