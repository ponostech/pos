/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers.sales;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextField;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import ponospos.entities.Customer;
import ponospos.entities.Invoice;
import ponospos.entities.InvoiceItem;
import ponospos.entities.Product;
import ponospos.entities.Stock;
import singletons.Auth;
import util.TransactionType;

/**
 * FXML Controller class
 *
 * @author Sawmtea
 */
public class CheckoutController extends AnchorPane
 implements CustomerSelectDialog.CustomerSelectionInterface{

   

    public interface CheckoutListener{
        public void onPay(Invoice invoice);
    }
    
    @FXML
    private TableView<InvoiceItem> checkoutTable;
    @FXML
    private TableColumn<InvoiceItem, String> itemCol;
    @FXML
    private TableColumn<InvoiceItem, Integer> qtyCol;
    @FXML
    private TableColumn<InvoiceItem, InvoiceItem> actionCol;
    @FXML
    private JFXTextField customerField;
    @FXML
    private Button customerSelectBtn;
    @FXML
    private JFXTextField subTotalField;
    @FXML
    private JFXTextField discountField;
    @FXML
    private JFXTextField totalField;
    @FXML
    private JFXRadioButton amountRadio;
    @FXML
    private RadioButton percentRadio;
    @FXML
    private JFXButton payBtnField;

    private double subTotal;
    private double discount;
    private double total;
    
    private Customer selectedCustomer;

    private CheckoutListener listener;
    private StackPane root;
    private ObservableList<InvoiceItem>invoiceItems=FXCollections.observableArrayList();
    private ObservableList<Customer>customers=FXCollections.observableArrayList();
    public CheckoutController(CheckoutListener listener,StackPane root) {
        super();
        this.listener=listener;
        this.root=root;
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(this.getClass().getResource("/views/sales/checkout.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            Region parent = loader.load();
            checkoutTable.setItems(invoiceItems);
            
            itemCol.setCellValueFactory(e -> {
                Product p=e.getValue().getItem();
                String str=p.getName()+"\n"+p.getTax().doubleValue();
                return new SimpleStringProperty(str);
            });
            qtyCol.setCellValueFactory(e -> new SimpleObjectProperty<>((e.getValue().getQuantity())));
            actionCol.setCellFactory((TableColumn<InvoiceItem, InvoiceItem> param) -> new TableCell<InvoiceItem, InvoiceItem>() {
                @Override
                protected void updateItem(InvoiceItem item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText("");
                        setGraphic(null);
                    } else {
                        Button btn = new Button("", new FontAwesomeIconView(FontAwesomeIcon.TRASH));
                        setGraphic(btn);
                        btn.setOnAction(e -> {
                            InvoiceItem inv = invoiceItems.get(getIndex());
                            removeFromCheckout(inv);
                            calculateSubtotal();
                            subTotalField.setText(String.valueOf(subTotal));
                            totalField.setText(String.valueOf(total));
                        });
                    }
                }

               

            });
            this.customerSelectBtn.setOnAction(e->{
                CustomerSelectDialog d=new CustomerSelectDialog(CheckoutController.this);
                d.setCustomers(customers);
                d.show(root);
            });
            this.payBtnField.disableProperty().bind(
                    subTotalField.textProperty().isEmpty().or(totalField.textProperty().isEmpty())
            );
//            this.invoiceItems.addListener(new InvalidationListener() {
//                @Override
//                public void invalidated(Observable observable) {
//                        calculateSubtotal();
//                        subTotalField.setText(String.valueOf(subTotal));
//
//                }
//            });
            this.discountField.setOnKeyReleased(e->{
                
                try{ 
                    double val=Double.parseDouble(discountField.getText());
                    boolean isExceed=false;
                    if (percentRadio.isSelected()) {
                        if (val>100){
                            isExceed=true;
                            discountField.clear();
                        } 
                        else
                            discount=subTotal*(val/100);
                        
                    }else{
                        if (val>subTotal) {
                            isExceed = true;
                            discountField.clear();
                        }
                        discount=val;
                    }
                    if (isExceed) {
                        discount=0;
                    }
                    calculateSubtotal();
                    subTotalField.setText(String.valueOf(subTotal));
                    totalField.setText(String.valueOf(total));
                }catch(NumberFormatException ex){
                    discountField.clear();
                }
            });
        } catch (IOException ex) {
            Logger.getLogger(CheckoutController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void removeFromCheckout(InvoiceItem get) {
        invoiceItems.remove(get);
        subTotalField.setText(Double.toString(subTotal));
    }

    private boolean productIsExistIncheckout(Product p) {
        for (InvoiceItem item : invoiceItems) {
            if (item.getItem().equals(p)) {
                return true;
            }
        }
        return false;
    }
    public void addItemsInCheckout(Product product,int qty){
        if (productIsExistIncheckout(product)) {
            addIntoCheckout(product, qty);
            subTotalField.setText(String.valueOf(subTotal));
        } else {
            InvoiceItem item = new InvoiceItem();
            item.setItem(product);
            item.setQuantity(qty);
            invoiceItems.add(item);
            subTotalField.setText(String.valueOf(subTotal));
        }
        calculateSubtotal();
        subTotalField.setText(String.valueOf(subTotal));
        totalField.setText(String.valueOf(total));

//        for (InvoiceItem item : invoiceItems) {
//            calculateSubtotal(item.getItem(), item.getQuantity());
//        }
    }
    public void disPlayCustomer(Customer c){
        this.customerField.setText(c.getFirstName()+" "+c.getLastName());
        this.selectedCustomer=c;
    }
    
    private void addIntoCheckout(Product p, int qty) {
        InvoiceItem invItem = getInvoiceByProduct(p);
        int sum = invItem.getQuantity() + qty;
        invItem.setQuantity(sum);
        checkoutTable.refresh();
    }
    private InvoiceItem getInvoiceByProduct(Product p) {
        for (InvoiceItem item : invoiceItems) {
            if (item.getItem().equals(p)) {
                return item;
            }
        }
        throw new RuntimeException("Product must exist in checkout");
    }
    private void calculateSubtotal(){
//        BigDecimal totalAmount=new BigDecimal(0);
//        for (InvoiceItem item : invoiceItems) {
//            BigDecimal amount = item.getItem().getSellingPrice().multiply(new BigDecimal(item.getQuantity()));
//            BigDecimal taxPercent=item.getItem().getTax().multiply(new BigDecimal(item.getQuantity()));
//            BigDecimal tax=amount.multiply(taxPercent.divide(new BigDecimal(100)));
//            BigDecimal subt=amount.subtract(tax);
//            
//            totalAmount.add(subt);
//        }
//        subTotal=totalAmount.floatValue();
        double totalAmount=0;
        for (InvoiceItem item : invoiceItems) {
            double amount = item.getItem().getSellingPrice().doubleValue();
            amount*=item.getQuantity();
            double percent=item.getItem().getTax().doubleValue();
            percent*=item.getQuantity();
            
            double tax=amount*(percent/100);
            
            totalAmount+=(amount-tax);
        }
        subTotal=totalAmount;
        total=subTotal-discount;
        
    }
    
    public void calculateSubtotal(Product item, int qty) {
        
        BigDecimal totalAmount = new BigDecimal(subTotal);
        BigDecimal price = item.getSellingPrice();
        BigDecimal taxPercent = item.getTax().multiply(new BigDecimal(qty));

        BigDecimal amount = price.multiply(new BigDecimal(qty));
        BigDecimal amountToDeduct=amount.multiply(taxPercent.divide(new BigDecimal(100)));
        totalAmount.add(
                amount.subtract(amountToDeduct)
        );
        subTotal = totalAmount.floatValue();
        String str = NumberFormat.getCurrencyInstance(new Locale("en","in")).format(subTotal);
        subTotalField.setText(str);
    }
    
    public Customer geSelectedCustomer(){
        return this.selectedCustomer;
    }
    
    @FXML
    public void onPayBtnClick(ActionEvent e){
        Invoice invoice=new Invoice();
        invoice.setCustomer(selectedCustomer);
        invoice.setTotal(new BigDecimal(total));
        invoice.setDiscount(new BigDecimal(discount));
        invoice.setInvoiceItem(invoiceItems);
        invoice.setInvoiceDate(new Date(System.currentTimeMillis()));
        invoice.setSoldBy(Auth.getInstance().getUser());
        invoice.setTax(new BigDecimal(subTotal));
        deductStock(invoice);
        for (InvoiceItem item : invoiceItems) {
            item.setInvoice(invoice);
        }
        listener.onPay(invoice);
               
    }
    private void deductStock(Invoice invoice){
        invoice.setStocks(new ArrayList<>());
        
        for (InvoiceItem item : checkoutTable.getItems()) {
            Stock stock = new Stock();
            stock.setCreatedAt(new Date(System.currentTimeMillis()));
            stock.setInvoice(invoice);
            stock.setStore(Auth.getInstance().getStore());
            stock.setUpdateAt(new Date(System.currentTimeMillis()));
            stock.setTransactionType(TransactionType.INVOICE.toString());
            stock.setProduct(item.getItem());
            stock.setQuantity(-item.getQuantity());
            stock.setUser(Auth.getInstance().getUser());
            invoice.getStocks().add(stock);
        }
    }
    @Override
    public void onSelect(Customer customer) {
        this.disPlayCustomer(customer);
    }


    public void setCustomers(List<Customer>customers){
        this.customers.clear();
        this.customers.addAll(customers);
    }
    public void reset(){
        this.subTotal=0;
        this.subTotalField.clear();
        this.discount=0;
        this.discountField.clear();
        this.total=0;
        this.totalField.clear();
        this.selectedCustomer=null;
        this.customerField.clear();
        this.invoiceItems.clear();
    }

    public void setSelectedCustomer(Customer selectedCustomer) {
        this.selectedCustomer = selectedCustomer;
    }
    private void clearIfInparseable(JFXTextField field) {
        try{
            Double.parseDouble(field.getText());
        }catch(NumberFormatException e){
            field.clear();
        }
    }


    
}
