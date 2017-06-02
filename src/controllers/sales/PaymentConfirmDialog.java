/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers.sales;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXTextArea;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import ponospos.entities.Customer;
import ponospos.entities.Invoice;
import ponospos.entities.InvoiceItem;
import ponospos.entities.Payment;
import util.InvoiceStatus;
import util.PaymentMethod;

/**
 * FXML Controller class
 *
 * @author Sawmtea
 */
public class PaymentConfirmDialog extends JFXDialog {

    public interface PaymentListener{
        public void onConfirmPayment(Invoice invoice,boolean print);
    }
    @FXML
    private TextField amountField;
    @FXML
    private TableView<InvoiceItem> itemTable;
    @FXML
    private TableColumn<InvoiceItem, String> itemCol;
    @FXML
    private TableColumn<InvoiceItem, Integer> quantityCol;
    @FXML
    private Label storeLabel;
    @FXML
    private Label customerLabel;
    @FXML
    private Label discountLabel;
    @FXML
    private Label dateLabel;
    @FXML
    private JFXButton backBtn;
    @FXML
    private JFXComboBox<String> paymentMethodField;
    @FXML
    private JFXButton paymentBtn;
    @FXML
    private JFXCheckBox receiptCheck;
    @FXML
    private Label vatLabel;
    @FXML
    private Label discountLabel2;
    @FXML
    private JFXTextArea remark;
    
    private PaymentListener listener;
    private ObservableList<InvoiceItem> items=FXCollections.observableArrayList();
    private ObservableList<String> paymentMethods=FXCollections.observableArrayList();
    private Invoice invoice;
    
    private double amount;
    public PaymentConfirmDialog(PaymentListener listener) {
        super();
        this.listener=listener;
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(this.getClass().getResource("/views/sales/payment_confirm_dialog.fxml"));
            loader.setController(this);
            Region region = loader.load();
            this.setContent(region);
            this.setOnDialogOpened(e->amountField.requestFocus());
            this.backBtn.setOnAction(e->close());
            this.paymentMethodField.setItems(paymentMethods);
            this.paymentMethods.addAll(
                    PaymentMethod.CASH_IN_HAND.toString(),
                    PaymentMethod.DEBIT_CARD.toString(),
                    PaymentMethod.CREDIT_CARD.toString(),
                    PaymentMethod.CHECK.toString()
            );
            this.paymentMethodField.getSelectionModel().selectFirst();
            this.itemTable.setItems(items);
            this.itemCol.setCellValueFactory(e->new SimpleStringProperty(e.getValue().getItem().getName()));
            this.quantityCol.setCellValueFactory(e->new SimpleObjectProperty<>(e.getValue().getQuantity()));
            
            this.amountField.setOnKeyReleased(e->{
                try{
                    Double.parseDouble(amountField.getText());
                }catch(NumberFormatException ec){
                    amountField.clear();
                }
            });
            
            this.paymentBtn.disableProperty().bind(
                    amountField.textProperty().isEmpty()
                            .or(paymentMethodField.getSelectionModel().selectedItemProperty().isNull())
            );
        } catch (IOException ex) {
            Logger.getLogger(PaymentConfirmDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
    
    }

    public void setInvoice(Invoice invoice){
        this.invoice=invoice;
    }
    public void fillField(){
        storeLabel.setText("");
        vatLabel.setText(Double.toString(invoice.getTax().doubleValue()));
        Customer c=invoice.getCustomer();
        if (c!=null) {
            customerLabel.setText(invoice.getCustomer().toString());
        }
        discountLabel.setText(invoice.getDiscount().toPlainString());
        amountField.setText(invoice.getTotal().toPlainString());
        items.addAll(invoice.getInvoiceItem());
        
        SimpleDateFormat format=new SimpleDateFormat("dd/MM/yy");
        dateLabel.setText(format.format(invoice.getInvoiceDate()));
        
        amountField.setText(Double.toString(invoice.getTotal().doubleValue()));
        this.amount=invoice.getTotal().doubleValue();
    }
    
    @FXML
    public void onConfirmPayment(ActionEvent e){
        amount=Double.parseDouble(amountField.getText().trim());
        
        Payment p=new Payment();
        p.setAmount(new BigDecimal(Double.parseDouble(amountField.getText())));
        p.setInvoice(invoice);
        p.setPayDate(invoice.getInvoiceDate());
        p.setAmount(new BigDecimal(amountField.getText().trim()));
        invoice.setPayment(p);
        invoice.setRemark(remark.getText().trim());
        invoice.setInvoiceDate(new Date(System.currentTimeMillis()));
        
        if (invoice.getTotal().doubleValue()==amount) {
            invoice.setStatus(InvoiceStatus.FULL_PAYMENT.toString());
        }else if(amount==0){
            invoice.setStatus(InvoiceStatus.PENDING.toString());
        }else{
            invoice.setStatus(InvoiceStatus.PARTIAL.toString());
        }
        
        
        this.close();
        listener.onConfirmPayment(invoice,receiptCheck.isSelected());
    }
    
}
