/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers.sales;

import com.jfoenix.controls.JFXDialog;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import java.io.Closeable;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
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
    private TableColumn<InvoiceItem, Double> taxCol;
    @FXML
    private TableColumn<InvoiceItem, Integer> qtyCol;
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
    private Label empLabel;
    @FXML
    private MaterialDesignIconView close;

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
            taxCol.setCellValueFactory(e->new SimpleObjectProperty<>(e.getValue().getItem().getTax().doubleValue()));
            qtyCol.setCellValueFactory(e->new SimpleObjectProperty<>(e.getValue().getQuantity()));
            totalCol.setCellValueFactory(e->{
                InvoiceItem value = e.getValue();
                BigDecimal price=value.getItem().getSellingPrice();
                price.multiply(new BigDecimal(value.getQuantity()));
                String str = NumberFormat.getCurrencyInstance(new Locale("en","IN")).format(price.doubleValue());
                return new SimpleStringProperty(str);
            });
            
            close.setOnMouseClicked(e->close());
        } catch (IOException ex) {
            Logger.getLogger(SalesDetailDialog.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void setInvoice(Invoice invoice){
        this.invoice=invoice;
        this.items.clear();
        System.out.println("InvoiceItem size:"+invoice.getInvoiceItem().size());
        this.items.addAll(invoice.getInvoiceItem());
        if (invoice.getCustomer()!=null) {
            
        this.customerLabel.setText(invoice.getCustomer().toString());
        }
        
        SimpleDateFormat fm=new SimpleDateFormat("dd MMM yyy");
        String str = fm.format(invoice.getInvoiceDate());
        this.dateLabel.setText(str);
        this.statusLabel.setText(invoice.getStatus());
        this.idLabel.setText(invoice.getId().toString());
        this.storeLabel.setText(invoice.getStocks().get(0).getStore().getName());
        this.empLabel.setText(invoice.getSoldBy().getUsername());
        itemTable.refresh();
    }
    
    
}
