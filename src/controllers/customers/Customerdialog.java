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
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import ponospos.entities.Customer;

/**
 * FXML Controller class
 *
 * @author Sawmtea
 */
public class Customerdialog extends JFXDialog{
    
    interface   CustomerDialogListener{
        public void onCreate(Customer customer);
        public void onEdit(Customer customer);
    }
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
    
    private boolean isEditPurpose;
    private Customer customer;
    
    private CustomerDialogListener listener;

    /**
     * Initializes the controller class.
     */
    
    public void Customerdialog(){
        try {
            FXMLLoader loader=new FXMLLoader();
            loader.setLocation(this.getClass().getResource("/views/customers/customer_dialog.fxml"));
            loader.setController(this);
            VBox root = loader.load();
            this.setContent(root);
            this.setTransitionType(DialogTransition.TOP);
            saveButton.setGraphic(new Glyph("FontAwesome", FontAwesome.Glyph.SAVE));
            doValidation();
        } catch (IOException ex) {
            Logger.getLogger(CustomersController.class.getName()).log(Level.SEVERE, null, ex);
        }
    } 
    
    @FXML
    private void onSaveClick(ActionEvent event) {        
        if (isEditPurpose) {
            saveButton.setText("Update Customer");
            customer.setFirstName(firstNameField.getText().trim());
            customer.setLastName(lastNameField.getText().trim());
            customer.setEmail(emailField.getText().trim());
            customer.setAddress(addressField.getText().trim());
            customer.setContact(contactField.getText().trim());
            listener.onEdit(customer);
        }else{
            saveButton.setText("Create Customer");
            clearAll();
            Customer c=new Customer();
            c.setFirstName(firstNameField.getText().trim());
            c.setLastName(lastNameField.getText().trim());
            c.setEmail(emailField.getText().trim());
            c.setAddress(addressField.getText().trim());
            c.setContact(contactField.getText().trim());
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
                        .and(lastNameField.textProperty().isEmpty())
        );
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
     public boolean isIsEditPurpose() {
        return isEditPurpose;
    }

    public void setIsEditPurpose(boolean isEditPurpose) {
        this.isEditPurpose = isEditPurpose;
    }

    public CustomerDialogListener getListener() {
        return listener;
    }

    public void setListener(CustomerDialogListener listener) {
        this.listener = listener;
    }
    
    public void clearAll(){
        firstNameField.clear();
        lastNameField.clear();
        emailField.clear();
        addressField.clear();
        contactField.clear();
    }
    
}
