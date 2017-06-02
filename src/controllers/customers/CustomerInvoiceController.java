/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers.customers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import controllers.PonosControllerInterface;
import controllers.sales.CustomerSelectDialog;
import controllers.users.UsersController;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import org.controlsfx.control.MaskerPane;
import ponospos.entities.Customer;
import ponospos.entities.Payment;
import singletons.PonosExecutor;
import tasks.customers.FetchAllCustomerTask;
import tasks.payments.FetchAllPaymentTask;
import tasks.payments.FindCustomerPaymentTask;
import util.DateConverter;

/**
 * FXML Controller class
 *
 * @author Sawmtea
 */
public class CustomerInvoiceController extends AnchorPane
        implements PonosControllerInterface,
        CustomerSelectDialog.CustomerSelectionInterface{

    @FXML
    private JFXTextField customerField;
    @FXML
    private Button customerBtn;
    @FXML
    private JFXDatePicker fromDatePicker;
    @FXML
    private JFXDatePicker toDatePicker;
    @FXML
    private JFXButton filterBtn;
    @FXML
    private TableView<Payment> paymentTable;
    @FXML
    private TableColumn<Payment, Integer> idCol;
    @FXML
    private TableColumn<Payment, String> dateCol;
    @FXML
    private TableColumn<Payment, Customer> customerCol;
    @FXML
    private TableColumn<Payment, String> addressCol;
    @FXML
    private TableColumn<Payment, Integer> invoiceCol;
    @FXML
    private TableColumn<Payment, String> amountCol;
    @FXML
    private TableColumn<Payment, String> statusCol;
    @FXML
    private TableColumn<Payment, Payment> actionCol;
    
    private ObservableList<Payment> payments=FXCollections.observableArrayList();
    private ObservableList<Customer> customers=FXCollections.observableArrayList();
    private StackPane root;
    private MaskerPane mask;
    private Customer selectedCustomer;

    public CustomerInvoiceController(StackPane root,MaskerPane mask) {
        this.root = root;
        this.mask = mask;
        try {
            this.mask = mask;
            this.root = root;
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(this.getClass().getResource("/views/customers/customer_invoice.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();

        } catch (IOException ex) {
            Logger.getLogger(UsersController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    
    @FXML
    private void onCustomerbtnClick(ActionEvent event) {
        CustomerSelectDialog d=new CustomerSelectDialog(this);
        d.setCustomers(customers);
        d.show(root);
    }
    @FXML
    private void onRefreshBtnClick(ActionEvent event) {
        fetchAll();
    }

    @FXML
    private void onFilterBtnClick(ActionEvent event) {
        FindCustomerPaymentTask task=new FindCustomerPaymentTask();
        task.customer=selectedCustomer;
        task.from=DateConverter.toUtilDate(fromDatePicker.getValue());
        task.to=DateConverter.toUtilDate(toDatePicker.getValue());
        task.setOnSucceeded(e->{
            payments.clear();
            payments.addAll(task.getValue());
        });
        task.setOnFailed(e->task.getException().printStackTrace(System.err));
        mask.visibleProperty().bind(task.runningProperty());
        PonosExecutor.getInstance().getExecutor().submit(task);
    }

    @Override
    public void initDependencies() {
    }

    @Override
    public void initControls() {
        this.paymentTable.setItems(payments);
        idCol.setCellValueFactory(e->new SimpleObjectProperty<>(e.getValue().getId()));
        dateCol.setCellValueFactory(e->{
            Payment p = e.getValue();
            Date paymentDate = p.getPayDate();
            SimpleDateFormat fmt=new SimpleDateFormat("EEE dd MMM yy ");
            String str = fmt.format(paymentDate);
            return new SimpleStringProperty(str);
        });
        customerCol.setCellValueFactory(e->new SimpleObjectProperty<>(e.getValue().getInvoice().getCustomer()));
        addressCol.setCellValueFactory(e->{
            Payment p = e.getValue();
            Customer customer = p.getInvoice().getCustomer();
            if (customer!=null) 
                return  new SimpleStringProperty(customer.getAddress());
            else
                return null;
        });
        invoiceCol.setCellValueFactory(e->new SimpleObjectProperty<>(e.getValue().getInvoice().getId()));
        amountCol.setCellValueFactory(e->{
            Payment p = e.getValue();
            String str = NumberFormat.getCurrencyInstance(new Locale("en", "in")).format(p.getAmount().doubleValue());
            return new SimpleStringProperty(str);
        });
        statusCol.setCellValueFactory(e->new SimpleStringProperty(e.getValue().getInvoice().getStatus()));
        actionCol.setCellFactory((TableColumn<Payment, Payment> param)
                -> new TableCell<Payment, Payment>() {
            FontAwesomeIconView viewIcon = new FontAwesomeIconView(FontAwesomeIcon.EYE);
            FontAwesomeIconView editIcon = new FontAwesomeIconView(FontAwesomeIcon.EDIT);
            FontAwesomeIconView delIcon = new FontAwesomeIconView(FontAwesomeIcon.TRASH);
            Button viewBtn = new Button("", viewIcon);
            Button editBtn = new Button("", editIcon);
            Button delBtn = new Button("", delIcon);

            @Override
            protected void updateItem(Payment item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    viewBtn.setOnAction(e -> {

                    });
                    editBtn.setOnAction(e -> {
                        
                    });
                    delBtn.setOnAction(e -> {
                       
                    });
                    setGraphic(new HBox(5, viewBtn, editBtn, delBtn));
                }
            }

        }
        );
    }

    @Override
    public void bindControls() {
        this.filterBtn.disableProperty().bind(customerField.textProperty().isEmpty().or
        (fromDatePicker.valueProperty().isNull()).or(toDatePicker.valueProperty().isNull()));
    }

    @Override
    public void hookupEvent() {
        
    }

    @Override
    public void controlFocus() {
    }
    
    public void fetchAll(){
        tasks.customers.FetchAllCustomerTask task=new tasks.customers.FetchAllCustomerTask();
        task.setOnSucceeded(e->{
            customers.clear();
            customers.addAll(task.getValue());
        });
        mask.visibleProperty().bind(task.runningProperty());
        task.setOnFailed(e->task.getException().printStackTrace(System.err));
        //getall payment
        FetchAllPaymentTask task1=new FetchAllPaymentTask();
        task1.setOnSucceeded(e->{
            payments.clear();
            payments.addAll(task1.getValue());
        });
        mask.visibleProperty().bind(task1.runningProperty());
        task1.setOnFailed(e->task1.getException().printStackTrace(System.err));
        
        PonosExecutor.getInstance().getExecutor().submit(task);
        PonosExecutor.getInstance().getExecutor().submit(task1);
    }

    @Override
    public void onSelect(Customer customer) {
        this.selectedCustomer=customer;
        customerField.setText(customer.getFirstName());
    }
}
