/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers.customers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import ponospos.entities.Customer;

/**
 * FXML Controller class
 *
 * @author Sawmtea
 */
public class CustomerDialog extends JFXDialog{ 

    
    interface   CustomerDialogListener{
        public void onCreate(Customer customer);
        public void onEdit(Customer customer);
    }
    @FXML
    private VBox root;
    @FXML
    private Label topLabel;
    @FXML
    private JFXTextField firstNameField;
    @FXML
    private JFXTextField lastNameField;
    @FXML
    private JFXTextField emailField;
    @FXML
    private JFXTextArea addressField;
    @FXML
    private JFXTextField contactField;
    @FXML
    private JFXButton saveButton;
    @FXML
    private JFXButton cancelButton;
    @FXML
    private ImageView close;
    
    private boolean isEditPurpose;
    private boolean isViewPurpose;
    private Customer customer;
    
    private CustomerDialogListener listener;

    /**
     * Initializes the controller class.
     * @param listener
     */
    
    public CustomerDialog(CustomerDialogListener listener){
        this.listener=listener;
        try {
            FXMLLoader loader=new FXMLLoader();
            loader.setLocation(this.getClass().getResource("/views/customers/customer_dialog.fxml"));
            loader.setController(this);
            Region parent = loader.load();
            this.setContent(parent);
            saveButton.setGraphic(new Glyph("FontAwesome", FontAwesome.Glyph.SAVE).color(Color.CORAL).size(20));
            doValidation();
            this.setTransitionType(DialogTransition.TOP);
            close.setOnMouseClicked(e->this.close());
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
            Logger.getLogger(CustomersController.class.getName()).log(Level.SEVERE, null, ex);
        }
    } 
   
    @FXML
    private void onSaveClick(ActionEvent event) {        
        
        if (isEditPurpose) {
            customer.setFirstName(firstNameField.getText().trim());
            customer.setLastName(lastNameField.getText().trim());
            customer.setEmail(emailField.getText().trim());
            customer.setAddress(addressField.getText().trim());
            customer.setContact(contactField.getText().trim());
            customer.setUpdatedAt(new Date(System.currentTimeMillis()));
            listener.onEdit(customer);
        }else{
            Customer c=new Customer();
            c.setFirstName(firstNameField.getText().trim());
            c.setLastName(lastNameField.getText().trim());
            c.setEmail(emailField.getText().trim());
            c.setAddress(addressField.getText().trim());
            c.setContact(contactField.getText().trim());
            c.setCreatedAt(new Date(System.currentTimeMillis()));
            c.setUpdatedAt(new Date(System.currentTimeMillis()));
            listener.onCreate(c);
        }
        this.close();
    }

    @FXML
    private void onCancelClick(ActionEvent event) {
        this.close();
    }
    private void doValidation(){
        saveButton.disableProperty().bind(
                firstNameField.textProperty().isEmpty()
                        .or(lastNameField.textProperty().isEmpty())
        );
    }
    
    public void setIsViewPurpose(boolean tobeTrue){
        this.isViewPurpose=tobeTrue;
        if (isViewPurpose) {
            topLabel.setText("User info");
            saveButton.setVisible(false);
            
        }
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
     public boolean isEditPurpose() {
        return isEditPurpose;
    }

    public void isEditPurpose(boolean isEditPurpose) {
        this.isEditPurpose = isEditPurpose;
    }

    public CustomerDialogListener getListener() {
        return listener;
    }

    public void setListener(CustomerDialogListener listener) {
        this.listener = listener;
    }
    void setEditPurpose(boolean b) {
       this.isEditPurpose=b;
    }
    
    public void clearAll(){
        firstNameField.clear();
        lastNameField.clear();
        emailField.clear();
        addressField.clear();
        contactField.clear();
    }

    @Override
    public void show() {
        if (isEditPurpose && isViewPurpose) {
            firstNameField.setText(customer.getFirstName());
            lastNameField.setText(customer.getLastName());
            emailField.setText(customer.getEmail());
            addressField.setText(customer.getAddress());
            contactField.setText(customer.getContact());
        }else{
            clearAll();
        }
        super.show();
    }

    @Override
    public void show(StackPane dialogContainer) {
        if (isEditPurpose) {
            saveButton.setText("Update");
            firstNameField.setText(customer.getFirstName());
            lastNameField.setText(customer.getLastName());
            emailField.setText(customer.getEmail());
            addressField.setText(customer.getAddress());
            contactField.setText(customer.getContact());
        }else if(isViewPurpose){
            saveButton.setVisible(false);
            firstNameField.setText(customer.getFirstName());
            lastNameField.setText(customer.getLastName());
            emailField.setText(customer.getEmail());
            addressField.setText(customer.getAddress());
            contactField.setText(customer.getContact());
        }
        else{
            saveButton.setText("Create");
            clearAll();
        }
        super.show(dialogContainer); 
    }
    public void delegateFocus(){
        firstNameField.requestFocus();
    }
    
    
}
