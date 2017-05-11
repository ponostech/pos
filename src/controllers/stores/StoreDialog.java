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
        this.title.setText("Update store");
        this.isEditPurpose=true;
        this.positiveBtn.setText("Update");
        return this;
    }
    public StoreDialog createStore(){
        this.title.setText("Create");
        this.isCreatePurpose=true;
        this.positiveBtn.setText("Create");
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
            
        }
    }
    @FXML
    public void onNegativeBtnClick(ActionEvent event){
        this.close();
    }
    
}