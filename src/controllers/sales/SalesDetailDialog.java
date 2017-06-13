/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers.sales;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Currency;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Region;
import ponospos.entities.Invoice;
import ponospos.entities.InvoiceItem;
import ponospos.entities.Product;

/**
 * FXML Controller class
 *
 * @author Sawmtea
 */
public class SalesDetailDialog extends JFXDialog {

    @FXML
    private TableView<InvoiceItem> itemTable;
    @FXML
    private TableColumn<InvoiceItem, Product> itemCol;
    @FXML
    private TableColumn<InvoiceItem, String> taxCol;
    @FXML
    private TableColumn<InvoiceItem, Integer> qtyCol;
    @FXML
    private TableColumn<InvoiceItem, String> priceCol;
    @FXML
    private TableColumn<InvoiceItem, String> totalCol;
    @FXML
    private Label dateLabel;
    @FXML
    private Label customerLabel;
    @FXML
    private Label statusLabel;
    @FXML
    private Label storeLabel;
    @FXML
    private Label idLabel;
    @FXML
    private Label discountLabel;
    @FXML
    private Label amountLabel;
    @FXML
    private Label empLabel;
    @FXML
    private MaterialDesignIconView close;
    @FXML
    private JFXButton closeBtn;

    private Invoice invoice; 
    private ObservableList<InvoiceItem> items=FXCollections.observableArrayList();
    public SalesDetailDialog() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(this.getClass().getResource("/views/sales/sales_detail.fxml"));
            loader.setController(this);
            Region region = loader.load();
            this.setContent(region);
            this.setOnDialogOpened(e -> close.requestFocus());
            
            itemTable.setItems(items);
            itemCol.setCellValueFactory(e->new SimpleObjectProperty<>(e.getValue().getItem()));
            priceCol.setCellValueFactory(e->{
                InvoiceItem iv = e.getValue();
                double sp = iv.getItem().getSellingPrice().doubleValue();
                String c = NumberFormat.getCurrencyInstance(new Locale("en","in")).format(sp);
                
                return new SimpleStringProperty(c);
            });
            taxCol.setCellValueFactory(e->{
                InvoiceItem ini = e.getValue();
                double percent = ini.getItem().getTax().doubleValue();
                int qty = ini.getQuantity();
                double sp = ini.getItem().getSellingPrice().doubleValue();
                
                double discountAmount=sp*percent/100;
                double amount=(sp*qty)-((sp*qty)-(discountAmount*qty));
                String str = NumberFormat.getCurrencyInstance(new Locale("en","in")).format(amount);
                return new SimpleStringProperty(str);
            });
            qtyCol.setCellValueFactory(e->new SimpleObjectProperty<>(e.getValue().getQuantity()));
            totalCol.setCellValueFactory(e->{
                InvoiceItem value = e.getValue();
                BigDecimal price=value.getItem().getSellingPrice();
                price=price.multiply(new BigDecimal(value.getQuantity()));
                String str = NumberFormat.getCurrencyInstance(new Locale("en","IN")).format(price.doubleValue());
                return new SimpleStringProperty(str);
            });
            
            close.setOnMouseClicked(e->close());
            closeBtn.setOnAction(e->close());
        } catch (IOException ex) {
            Logger.getLogger(SalesDetailDialog.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void setInvoice(Invoice invoice){
        this.invoice=invoice;
        this.items.clear();
        System.out.println("InvoiceItem size:"+invoice.getInvoiceItem().size());
        this.items.addAll(invoice.getInvoiceItem());
        if (invoice.getCustomer()!=null) 
            this.customerLabel.setText("Customer : "+invoice.getCustomer().toString());
        else
            this.customerLabel.setText("Customer : NA");
        SimpleDateFormat fm=new SimpleDateFormat("EEE dd MMM yyy");
        String str = fm.format(invoice.getInvoiceDate());
        this.dateLabel.setText("Date : "+str);
        this.statusLabel.setText("Payment Status : "+invoice.getStatus());
        this.idLabel.setText("Invoice ID : "+invoice.getId().toString());
        
        this.empLabel.setText("Employee : "+invoice.getSoldBy().getUsername());
        String discountStr = NumberFormat.getCurrencyInstance(new Locale("en","in")).format(invoice.getDiscount().doubleValue());
        this.discountLabel.setText("Discount Amount : "+discountStr);
        String amount = NumberFormat.getCurrencyInstance(new Locale("en", "in")).format(invoice.getPayment().getAmount().doubleValue());
        this.amountLabel.setText("Paid Amount : "+amount);
        itemTable.refresh();
    }
    
    
    
    
}
