/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers.sales;

import Messages.CustomerMessage;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextField;
import controllers.PonosControllerInterface;
import controllers.customers.CustomerDialog;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.controlsfx.control.MaskerPane;
import org.controlsfx.control.Notifications;
import ponospos.entities.Customer;
import ponospos.entities.Product;
import singletons.PonosExecutor;
import tasks.customers.FetchAllCustomerTask;
import tasks.products.FetchAllTask;

/**
 * FXML Controller class
 *
 * @author Sawmtea
 */
public class SellController extends AnchorPane implements 
        CustomerDialog.CustomerDialogListener,CustomerSelectDialog.CustomerSelectionInterface, 
        PonosControllerInterface {

    private StackPane root;
    private MaskerPane mask;
    @FXML
    private VBox vbox;
    
    private ProductContainer productContainer;
    
    private List<Product> products=new ArrayList<>();
    @FXML
    private AnchorPane AnchorPane;
    @FXML
    private JFXButton customerBtn;
    @FXML
    private TableView<?> checkoutTable;
    @FXML
    private TableColumn<?, ?> itemCol;
    @FXML
    private TableColumn<?, ?> qtyCol;
    @FXML
    private TableColumn<?, ?> actionCol;
    @FXML
    private JFXTextField customerField;
    @FXML
    private JFXTextField taxField;
    @FXML
    private JFXTextField totalField;
    @FXML
    private JFXRadioButton percentField;
    @FXML
    private JFXRadioButton amountField;
    @FXML
    private JFXTextField totalAmountField;
    @FXML
    private Button selectCustomerBtn;
    
    private ObservableList customers=FXCollections.observableArrayList();
    
    public SellController(MaskerPane mask,StackPane root){
        try {
            this.mask=mask;
            this.root=root;
            this.productContainer=new ProductContainer();
            FXMLLoader loader =new FXMLLoader();
            loader.setLocation(this.getClass().getResource("/views/sales/sell.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
            vbox.getChildren().add(productContainer);
        } catch (IOException ex) {
            Logger.getLogger(SellController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void initDependencies() {
    }

    @Override
    public void initControls() {
    }

    @Override
    public void bindControls() {
    }

    @Override
    public void hookupEvent() {
        customerBtn.setOnAction(e->{
            CustomerDialog d=new CustomerDialog(this);
            d.show(root);
        });
        selectCustomerBtn.setOnAction(e->{
            CustomerSelectDialog d=new CustomerSelectDialog(this);
            d.setCustomers(customers);
            d.show(root);
        });
    }

    @Override
    public void controlFocus() {
    }
    
    public void fetchAll(){
        FetchAllTask task=new FetchAllTask();
        task.setOnSucceeded(e->{
            this.productContainer.populateData(task.getValue());
        });
        task.setOnFailed(e->task.getException().printStackTrace(System.err));
        mask.visibleProperty().bind(task.runningProperty());
        tasks.customers.FetchAllCustomerTask t2=new FetchAllCustomerTask();
        t2.setOnSucceeded(e->{
            this.customers.clear();
            this.customers.addAll(t2.getValue());
        });
        t2.setOnFailed(e->task.getException().printStackTrace(System.err));
        mask.visibleProperty().bind(task.runningProperty());
        PonosExecutor.getInstance().getExecutor().submit(task);
        PonosExecutor.getInstance().getExecutor().submit(t2);
        
        
    }

    @Override
    public void onCreate(Customer customer) {
        tasks.customers.CreateTask task=new tasks.customers.CreateTask();
        task.setOnSucceeded(e->{
            Notifications.create().title(CustomerMessage.CREATE_SUCCESS_TITLE)
                    .text(CustomerMessage.CREATE_SUCCESS_MESSAGE)
                    .showInformation();
            customers.add(task.getValue());
        });
        task.setCustomer(customer);
        task.setOnFailed(e->task.getException().printStackTrace(System.err));
        mask.visibleProperty().bind(task.runningProperty());
        PonosExecutor.getInstance().getExecutor().submit(task);
    }

    @Override
    public void onEdit(Customer customer) {
        //TODO:nothing
    }

    @Override
    public void onSelect(Customer customer) {
        this.customerField.setText(customer.getFirstName() + customer.getLastName());
    }
}
