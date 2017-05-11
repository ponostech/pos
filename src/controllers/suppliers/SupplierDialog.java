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
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
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
        } catch (IOException ex) {
            Logger.getLogger(SupplierDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void setModels(Supplier models){
        this.model=models;
    }
    public void createPurpose(){
        this.positiveBtn.setText("Create supplier");
        this.isCreatePurpose=true;
    }
    
    public void editPurpose(){
        this.positiveBtn.setText("Update supplier");
        this.isEditPurpose=true;
    }
    //Cascading methods
    public SupplierDialog isCreate(){
        createPurpose();
        return this;
    }
    public SupplierDialog isEdit(){
        editPurpose();
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
         
    }

    @FXML
    private void onCancelBtnClick(ActionEvent event) {
        model=null;
        this.close();
    }

    
}
