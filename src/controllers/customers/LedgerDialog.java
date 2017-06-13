/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers.customers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXDialog;
import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import ponospos.entities.Customer;
import ponospos.entities.Invoice;
import ponospos.entities.Payment;
import util.DateConverter;
import util.InvoiceStatus;

/**
 * FXML Controller class
 *
 * @author Sawmtea
 */
public class LedgerDialog extends JFXDialog implements Initializable {

    @FXML
    private TableView<Payment> ledgarTable;
    @FXML
    private TableColumn<Payment, Integer> snCol;
    @FXML
    private TableColumn<Payment, Date> dateCol;
    @FXML
    private TableColumn<Payment, String> descCol;
    @FXML
    private TableColumn<Payment, String> debitCol;
    @FXML
    private TableColumn<Payment, String> creditCol;
    @FXML
    private TableColumn<Payment, String> balanceCol;
    @FXML
    private Label namelabel;
    @FXML
    private Label addressLabel;
    @FXML
    private Label emailLabel;
    @FXML
    private Label contactlabel;
    @FXML
    private Label balanceLabel;
    @FXML
    private JFXDatePicker fromDate;
    @FXML
    private JFXDatePicker toDate;
    @FXML
    private JFXButton filterBtn;
    @FXML
    private Button close;

    private Region region;
    private StackPane root;
    private Customer customer;
    
    private ObservableList<Payment> payments=FXCollections.observableArrayList();
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    
    }

    public LedgerDialog (){
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(this.getClass().getResource("/views/customers/ledger.fxml"));
            loader.setController(this);
            region = loader.load();
            this.setContent(region);

        } catch (IOException ex) {
            Logger.getLogger(LedgerDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
    public void setCustomer(Customer c){
        this.customer=c;
        for (Invoice invoice : customer.getInvoices()) {
            payments.add(invoice.getPayment());
        }
        populateTable();
    }
    private void populateTable(){
        ledgarTable.setItems(payments);
        snCol.setCellValueFactory(e->new SimpleObjectProperty());
        dateCol.setCellValueFactory(e->{
            Payment p = e.getValue();
            Date d = p.getPayDate();
            SimpleDateFormat fm=new SimpleDateFormat("dd/MM/yy hh:mm a");
            String str = fm.format(d);
            return new SimpleObjectProperty(str);
        });
        descCol.setCellValueFactory(e->{
            Invoice inv = e.getValue().getInvoice();
            String str = inv.getStatus();
            return new SimpleStringProperty(str);
        });
        debitCol.setCellValueFactory(e->{
            Payment val = e.getValue();
            String str = NumberFormat.getCurrencyInstance(new Locale("en","in")).format(val.getAmount().doubleValue());
            return new SimpleStringProperty(str);
        });
        creditCol.setCellValueFactory(e->{
            Payment val = e.getValue();
            String str = NumberFormat.getCurrencyInstance(new Locale("en", "in")).format(val.getInvoice().getTotal().doubleValue());
            return new SimpleStringProperty(str);
        });
        balanceCol.setCellValueFactory(e->{
            e.getValue();
            String str = NumberFormat.getCurrencyInstance(new Locale("en","in")).format(calculateBalance(payments));
           return new SimpleStringProperty(str);
        });
    }

    private double calculateBalance(List<Payment> payments){
        double credit=0;
        double amount=0;
        double balance=0;
        for (Payment payment : payments) {
            boolean pending = payment.getInvoice().getStatus().equalsIgnoreCase(InvoiceStatus.PARTIAL.toString())
                    || payment.getInvoice().getStatus().equalsIgnoreCase(InvoiceStatus.PENDING.toString());
            if (pending) {
                credit=credit+(payment.getAmount().doubleValue());
            }
            amount = amount+(payment.getAmount().doubleValue());
        }
        balance=amount-(credit);
        return balance;
    }
    public void setLabelTexts(){
        this.namelabel.setText("Customer Name : "+customer.getFirstName() + customer.getLastName());
        this.addressLabel.setText("Address : "+customer.getAddress());
        this.emailLabel.setText("Email : "+customer.getEmail());
        this.contactlabel.setText("Contact : "+customer.getContact());
        this.balanceLabel.setText("Balance : "+calculateBalance(payments));
    }
    public void hookupEvent(){
        filterBtn.disableProperty().bind(fromDate.valueProperty().isNull().or(toDate.valueProperty().isNull()));
        filterBtn.setOnAction(e->{
            ObservableList<Payment>filteredList=FXCollections.observableArrayList();
            Date from=DateConverter.toUtilDate(fromDate.getValue());
            Date to=DateConverter.toUtilDate(toDate.getValue());
            for (Payment payment : payments) {
                if (payment.getPayDate().after(from) && payment.getPayDate().before(to)) {
                    filteredList.add(payment);
                }
            }
            ledgarTable.setItems(filteredList);
        });
        close.setOnAction(e->close());
    }
    
    
}
