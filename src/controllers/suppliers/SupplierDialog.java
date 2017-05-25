/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers.suppliers;

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
import ponospos.entities.Supplier;

/**
 *
 * @author Sawmtea
 */
public class SupplierDialog extends JFXDialog{

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

    
    public interface SupplierDialogListener{
        
        public void onCreateSupplier(Supplier supplier);
        public void onEditSupplier(Supplier supplier);
    }
    private SupplierDialogListener listener;
    private boolean isCreatePurpose;
    private boolean isEditPurpose;
    private boolean isViewPurpose;
    private Supplier model;
    public SupplierDialog(SupplierDialogListener listener){
        try {
            this.listener=listener;
            FXMLLoader loader=new FXMLLoader();
            loader.setLocation(this.getClass().getResource("/views/suppliers/supplier_dialog.fxml"));
            loader.setController(this);
            Region region = loader.load();
            this.setContent(region);
            this.setOnDialogOpened(e->nameField.requestFocus());
            this.positiveBtn.disableProperty().bind(nameField.textProperty().isEmpty());
            this.closeBtn.setOnMouseClicked(e->SupplierDialog.this.close());
            doValidate();
        } catch (IOException ex) {
            Logger.getLogger(SupplierDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public SupplierDialog setModels(Supplier models){
        this.model=models;
        return this;
    }
    public void createPurpose(){
        this.positiveBtn.setText("Create");
        this.isCreatePurpose=true;
        this.title.setText("Create Supplier");
    }
    
    public void editPurpose(){
        this.positiveBtn.setText("Update");
        this.isEditPurpose=true;
        this.title.setText("Edit Supplier");
    }
    public void viewPurpose(){
        this.positiveBtn.setText("Close");
        this.isViewPurpose=true;
        this.title.setText("Supplier Info");
    }
    //Cascading methods
    public SupplierDialog isCreate(){
        createPurpose();
        return this;
    }
    public SupplierDialog isEdit(){
        editPurpose();
         nameField.setText(model.getName());
        addressField.setText(model.getAddress());
        contactField.setText(model.getContact());
        return this;
    }
    public SupplierDialog isView(){
        viewPurpose();
        nameField.setText(model.getName());
        addressField.setText(model.getAddress());
        contactField.setText(model.getContact());
        return this;
    }
    public SupplierDialog toUpdateModel(Supplier supplier){
        this.model=supplier;
        nameField.setText(model.getName());
        addressField.setText(model.getAddress());
        contactField.setText(model.getContact());
        
        return this;
    }
    //cascading method ends here
    
     @FXML
    private void onPositiveBtnClick(ActionEvent event) {
        this.close();
        if (isCreatePurpose) {
            Supplier supplier=new Supplier();
            supplier.setName(nameField.getText().trim());
            supplier.setAddress(addressField.getText().trim());
            supplier.setContact(contactField.getText().trim());
            supplier.setCreatedAt(new Date(System.currentTimeMillis()));
            listener.onCreateSupplier(supplier);
         }
         if (isEditPurpose) {
            model.setName(nameField.getText().trim());
            model.setAddress(addressField.getText().trim());
            model.setContact(contactField.getText().trim());
            listener.onEditSupplier(model);
         } 
         if (isViewPurpose) {
             //TODO::nothing
         }
         
    }

    @FXML
    private void onCancelBtnClick(ActionEvent event) {
        model=null;
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
