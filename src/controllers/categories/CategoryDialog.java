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
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import ponospos.entities.Category;

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
    
    
    private boolean isEdit;
    private boolean isCreate;
    private boolean isView;
    private Category category;
    private CategoryDialogListener listener;
    
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
            
            this.close();
            listener.onCreate(c);
        }else if (isEdit) {
            category.setName(nameField.getText().trim());
            category.setDescription(descriptionField.getText().trim());
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
         
         return this;
     }
    public CategoryDialog isToUpdate(Category cat){
        this.isEdit=true;
        this.positiveBtn.setText("Update");
        this.title.setText("Edit Category");
        this.category=cat;
        this.nameField.setText(cat.getName());
        this.descriptionField.setText(cat.getDescription());
        
        return this;
    }

    @FXML
    private void onNegativebtnClick(ActionEvent event) {
        this.close();
    }
    
  
}
