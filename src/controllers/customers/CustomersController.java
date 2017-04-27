/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers.customers;

import Messages.CustomerMessage;
import com.jfoenix.controls.JFXButton;
import controllers.modals.ConfirmDialog;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import jpa.CustomerJpa;
import org.controlsfx.control.Notifications;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import ponospos.entities.Customer;
import singletons.PonosExecutor;

/**
 * FXML Controller class
 *
 * @author Sawmtea
 */
public class CustomersController extends StackPane implements
        Customerdialog.CustomerDialogListener,ConfirmDialog.ConfirmDialogListener {

    @FXML
    private TableView<Customer> customerTable;
    @FXML
    private TableColumn<Customer, Integer> idCol;
    @FXML
    private TableColumn<Customer, String> fnameCol;
    @FXML
    private TableColumn<Customer, String> lnameCol;
    @FXML
    private TableColumn<Customer, String> emailCol;
    @FXML
    private TableColumn<Customer, String> addressCol;
    @FXML
    private TableColumn<Customer,String> contactCol;
    @FXML
    private TableColumn<Customer, String> createdCol;
    @FXML
    private TableColumn<Customer, String> updateCol;
    @FXML
    private TableColumn<Void, Customer> actionCol;
    @FXML
    private Button newButton;
    @FXML
    private Label noOfCustomerlabel;

    Customerdialog dialog;
    
    private ObservableList<Customer> customers=FXCollections.observableArrayList();
    /**
     * Initializes the controller class.
     */
      
    public CustomersController(){
        try {
            FXMLLoader loader=new FXMLLoader();
            loader.setLocation(this.getClass().getResource("/views/customers/customers.fxml"));
            loader.setController(this);
            Parent root = loader.load();
            this.getChildren().add(root);
            initTable();
            addListenersToControls();
        } catch (IOException ex) {
            Logger.getLogger(CustomersController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void initTable() {
        customerTable.setItems(customers);
        //geta all customer and assign the return value to customers list
        FetchAllCustomerTask task=new FetchAllCustomerTask();
        task.setOnSucceeded(c->{
            List<Customer> all = task.getValue();
            customers.clear();
            customers.addAll(all);
        });
        task.setOnFailed(e->task.getException().printStackTrace());
        
        PonosExecutor.getInstance().getExecutor().submit(task);
        
        idCol.setCellValueFactory(c->new SimpleObjectProperty<Integer>(c.getValue().getId()));
        fnameCol.setCellValueFactory(c->new SimpleStringProperty(c.getValue().getFirstName()));
        lnameCol.setCellValueFactory(c->new SimpleStringProperty(c.getValue().getLastName()));
        emailCol.setCellValueFactory(c->new SimpleStringProperty (c.getValue().getEmail()));
        addressCol.setCellValueFactory(c->new SimpleStringProperty(c.getValue().getAddress()));
        contactCol.setCellValueFactory(c->new SimpleStringProperty(c.getValue().getContact()));
        createdCol.setCellValueFactory(c->{
            Date date = c.getValue().getCreatedAt();
            
            return null;
        });
        updateCol.setCellValueFactory(c->{
            return null;
        });
         actionCol.setCellFactory((TableColumn<Void, Customer> param) -> new TableCell<Void,Customer>(){
            Button editBtn=new Button("Edit",new Glyph("FontAwesome",FontAwesome.Glyph.EDIT));
            Button delBtn=new Button("Delete",new Glyph("FontAwesome",FontAwesome.Glyph.TRASH));
            
            @Override
            protected void updateItem(Customer item, boolean empty) {
                if (empty) {
                    setText(null);
                    setGraphic(null);
                }else{
                    editBtn.setOnAction(e->{
                        
                    });
                    delBtn.setOnAction(e->{
                        ConfirmDialog dialog=new ConfirmDialog();
                        dialog.setListener(CustomersController.this);
                        dialog.setModel(customers.get(getIndex()));
                        dialog.show(CustomersController.this);
                    });
                    setGraphic(new HBox(editBtn,delBtn));
                }
            } 
        });
    }
    public void addListenersToControls(){
        customers.addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                noOfCustomerlabel.setText(Integer.toString(customers.size()));
            }
        });
    }
    @FXML
    public void onNewButtonClick(ActionEvent e){
//        dialog=new Customerdialog();
//        dialog.setListener(this);
//        dialog.show(CustomersController.this);
        Customerdialog d=new Customerdialog();
        d.setListener(this);
        d.show(CustomersController.this);
    }

    @Override
    public void onCreate(Customer customer) {
        CreateTask task=new CreateTask();
        task.setCustomer(customer);
        task.setOnFailed(c->task.getException().printStackTrace());
        task.setOnSucceeded(c->{
            customers.add(task.getValue());
            Notifications.create()
                    .title(CustomerMessage.CREATE_SUCCESS_TITLE)
                    .text(CustomerMessage.DELETE_SUCCESS_MESSAGE)
                    .showInformation();
        });
        PonosExecutor.getInstance().getExecutor().submit(task);
    }

    @Override
    public void onEdit(Customer customer) {
        UpdateTask task=new UpdateTask();
        task.setCustomer(customer);
        task.setOnFailed(c->task.getException().printStackTrace());
        task.setOnSucceeded(c->{
            customers.add(task.getValue());
            Notifications.create()
                    .title(CustomerMessage.UPDATE_SUCCESS_TITLE)
                    .text(CustomerMessage.UPDATE_SUCCESS_MESSAGE)
                    .showInformation();
        });
        PonosExecutor.getInstance().getExecutor().submit(task);
    }

    @Override
    public void onClickYes(Object obj) {
        DeleteTask task=new DeleteTask();
        task.setCustomer((Customer) obj);
        task.setOnFailed(c->task.getException().printStackTrace());
        task.setOnSucceeded(c->{
            customers.remove(task.getValue());
            Notifications.create()
                    .title(CustomerMessage.DELETE_SUCCESS_TITLE)
                    .text(CustomerMessage.DELETE_SUCCESS_MESSAGE)
                    .showInformation();
        });
        PonosExecutor.getInstance().getExecutor().submit(task);
    }
    
    public class CreateTask extends Task<Customer>{
        private Customer customer;
        @Override
        protected Customer call() throws Exception {
            return CustomerJpa.createCustomer(customer);
        }

        public void setCustomer(Customer customer) {
            this.customer = customer;
        }
        
    }
     public class UpdateTask extends Task<Customer>{
         private Customer customer;
        @Override
        protected Customer call() throws Exception {
            return CustomerJpa.updateCustomer(customer);
        }
        public void setCustomer(Customer customer) {
            this.customer = customer;
        }
    
    }
      public class DeleteTask extends Task<Customer>{
          private Customer customer;
        @Override
        protected Customer call() throws Exception {
            return CustomerJpa.deleteCustomer(customer);
        }
        public void setCustomer(Customer customer) {
            this.customer = customer;
        }
    
    }
      public class FetchAllCustomerTask extends Task<List<Customer>>{
        @Override
        protected List<Customer> call() throws Exception {
            return CustomerJpa.getAllCustomers();
        }
        
    }
}
