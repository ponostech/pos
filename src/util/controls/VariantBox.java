/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.controls;

import com.jfoenix.controls.JFXComboBox;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import ponospos.entities.Variant;

/**
 *
 * @author Sawmtea
 */
public class VariantBox extends HBox{

    public interface VariantBoxListener{
        public void add(Variant variant);
        public void remove(VariantBox variantBox);
        public void separateByComma(Variant v,String text);
    }
    private VariantBoxListener listener;
        private JFXComboBox<Variant> variantBox;
        private TextField inputField;
        private Button delBtn;
        
        private ObservableList<Variant> variants=FXCollections.observableArrayList();
        
        public VariantBox(VariantBoxListener listener) {
            super();
            this.listener=listener;
            this.setSpacing(10);
            this.setPadding(new Insets(5));
            
            variantBox=new JFXComboBox();
            variantBox.setItems(variants);
            
            variantBox.setPrefWidth(180);
            inputField=new TextField();
            inputField.setPrefWidth(180);
            
            delBtn=new Button("",new FontAwesomeIconView(FontAwesomeIcon.TRASH));
            
            getChildren().addAll(variantBox,inputField,delBtn);
            
            delBtn.setOnAction(e->listener.remove(this));
            inputField.setOnKeyReleased(e->{
                if (e.getCode()==KeyCode.COMMA) {
                    String text=inputField.getText().substring(0, inputField.getLength()-1);
                    inputField.clear();
                    
                    Variant selectedItem = variantBox.getSelectionModel().getSelectedItem();
                    if (selectedItem!=null) {
                        listener.separateByComma(selectedItem,text);
                    }else{
                        inputField.clear();
                    }
                }
            });
            variantBox.setOnAction(e->{
                listener.add(variantBox.getSelectionModel().getSelectedItem());
            });
            
        }
        public void setVariants(ObservableList<Variant> data){
            this.variants.clear();
            this.variants.addAll(data);
        }
        
    }
