/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers.sales;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXListView;
import controllers.customers.CustomersController;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import static org.eclipse.persistence.internal.helper.Helper.close;
import ponospos.entities.Customer;

/**
 * FXML Controller class
 *
 * @author Sawmtea
 */
public class CustomerSelectDialog extends JFXDialog {

    public interface CustomerSelectionInterface{
        public void onSelect(Customer customer);
    }
    @FXML
    private TextField searchField;
    @FXML
    private JFXListView<Customer> customerList;
    @FXML
    private JFXButton positiveBtn;
    @FXML
    private JFXButton negativeBtn;
    @FXML
    private MaterialDesignIconView close;
    
    private CustomerSelectionInterface listener;
    private ObservableList<Customer> customers=FXCollections.observableArrayList();
    public CustomerSelectDialog(CustomerSelectionInterface listener){
        this.listener=listener;
        try {
            FXMLLoader loader=new FXMLLoader();
            loader.setLocation(this.getClass().getResource("/views/sales/customer_select_dialog.fxml"));
            loader.setController(this);
            Region parent = loader.load();
            this.setContent(parent);
           
            this.setTransitionType(JFXDialog.DialogTransition.TOP);
            
            customerList.setItems(customers);
            close.setOnMouseClicked(e->this.close());
            this.setOnDialogOpened(e->searchField.requestFocus());
            positiveBtn.disableProperty().bind(customerList.getSelectionModel().selectedItemProperty().isNull());
            positiveBtn.setOnAction(e->{
                CustomerSelectDialog.this.close();
                listener.onSelect(customerList.getSelectionModel().getSelectedItem());
                    });
            negativeBtn.setOnAction(e->close());
            
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
            Logger.getLogger(CustomersController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setCustomers(List<Customer> customers) {
        this.customers.clear();
        this.customers.addAll(customers);
    }
    
    
      
    
}
