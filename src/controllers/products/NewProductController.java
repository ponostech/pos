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
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import util.controls.PonosTextfield;
import util.controls.VariantValueField;

/**
 * FXML Controller class
 *
 * @author Sawmtea
 */
public class NewProductController extends JFXDialog {

    private PonosTextfield variantValueField;

    @FXML
    private JFXButton newVariantBtn;
    
    @FXML 
    private VBox variantContainer;
    @FXML 
    private JFXButton addBtn;
    
    private PonosTextfield ponosTextField;
    @FXML
    private JFXComboBox<?> categoryCombo;
    @FXML
    private JFXTextField nameField;
    @FXML
    private JFXTextArea descriptionField;
    @FXML
    private JFXComboBox<?> supplierField;
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
    private TableView<?> variantTable;
    @FXML
    private Label title;
    @FXML
    private MaterialDesignIconView closeBtn;
    
    public NewProductController(){
         
        try {
            FXMLLoader loader=new FXMLLoader();
            loader.setLocation(this.getClass().getResource("/views/products/new_product.fxml"));
            loader.setController(this);
            Region region = loader.load();
            this.setContent(region);
            variantContainer.getChildren().add(new VariantBox());
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
                    popup.hide();
                    System.out.println(textField.getText());
                        });
                
                vb.getChildren().addAll(label,textField,btn);
                popup.setPopupContent(vb);
                popup.show(newVariantBtn, JFXPopup.PopupVPosition.TOP, JFXPopup.PopupHPosition.RIGHT);
            });
            addBtn.setOnAction(ev->{
                variantContainer.getChildren().add(new VariantBox());
            });
        } catch (IOException ex) {
            Logger.getLogger(NewProductController.class.getName()).log(Level.SEVERE, null, ex);
        }
            
    }
    private class VariantBox extends HBox{

        private JFXComboBox variantBox;
        private VariantValueField valueField;
        public VariantBox() {
            super();
            this.setSpacing(10);
            variantBox=new JFXComboBox();
            valueField=new VariantValueField();
            getChildren().addAll(variantBox,valueField);
        }
        
    }
}
