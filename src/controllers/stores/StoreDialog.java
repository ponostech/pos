
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers.stores;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import de.jensd.fx.glyphs.GlyphsBuilder;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import ponospos.entities.Stores;

/**
 * FXML Controller class
 *
 * @author Sawmtea
 */
public class StoreDialog extends JFXDialog{
    public interface StoreDialogListener{
        public void onCreate(Stores store);
        public void onUpdate(Stores store);
    }
    @FXML
    private Label title;
    @FXML
    private MaterialDesignIconView closeBtn;
    @FXML
    private JFXTextField nameField;
    @FXML
    private JFXTextArea addressField;
    @FXML
    private JFXTextField contactField;
    @FXML
    private JFXButton positiveBtn;
    @FXML
    private JFXButton negativeBtn;
    @FXML
    private MaterialDesignIconView close;
    
    private boolean isEditPurpose;
    private boolean isCreatePurpose;
    private boolean isViewPurpose;
    private StoreDialogListener listener;
    private Stores store;
    
    public StoreDialog(StoreDialogListener listener){
        this.listener=listener;
        try {
            FXMLLoader loader=new FXMLLoader();
            loader.setLocation(this.getClass().getResource("/views/stores/store_dialog.fxml"));
            loader.setController(this);
            Region region = loader.load();
            this.setContent(region);
            this.positiveBtn.disableProperty().bind(nameField.textProperty().isEmpty());
            this.setOnDialogOpened(e->nameField.requestFocus());
            this.close.setOnMouseClicked(e->this.close());
            doValidate();
        } catch (IOException ex) {
            Logger.getLogger(StoreDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void setListener(StoreDialogListener listener){
        this.listener=listener;
    }
    public StoreDialog model(Stores store){
        nameField.setText(store.getName());
        addressField.setText(store.getAddress());
        contactField.setText(store.getContact());
        this.store=store;
        return this;
    }
    public StoreDialog updateStore(){   
        this.title.setText("Edit Store");
        this.isEditPurpose=true;
        this.positiveBtn.setText("Update");
        return this;
    }
    public StoreDialog createStore(){
        this.title.setText("Create Store");
        this.isCreatePurpose=true;
        this.positiveBtn.setText("Create");
        return this;
    } 
    public StoreDialog viewStore(){
        this.title.setText("Store Info");
        this.isViewPurpose=true;
        this.positiveBtn.setText("Close");
        return this;
    } 
    
    @FXML
    public void onPositiveBtnClick(ActionEvent event){
        this.close();
        if (isEditPurpose) {
            store.setName(nameField.getText().trim());
            store.setAddress(addressField.getText().trim());
            store.setContact(contactField.getText().trim());
            
            listener.onUpdate(store);
        }else if(isCreatePurpose){
            Stores s=new Stores();
            s.setName(nameField.getText().trim());
            s.setAddress(addressField.getText().trim());
            s.setContact(contactField.getText().trim());
            s.setCreatedAt(new Date(System.currentTimeMillis()));
            listener.onCreate(s);
        }else{
            this.close();
        }
    }
    @FXML
    public void onNegativeBtnClick(ActionEvent event){
        this.close();
    }
     public void doValidate(){
        RequiredFieldValidator validator = new RequiredFieldValidator();
        validator.setMessage("Input Required");
        validator.setIcon(GlyphsBuilder.create(FontAwesomeIconView.class)
            .glyph(FontAwesomeIcon.WARNING)
            .size("1em")
            .styleClass("error")
            .build());
        this.nameField.setValidators(validator);
        this.nameField.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            if (!newValue) {
                this.nameField.validate();
            }
        });
    }
}
