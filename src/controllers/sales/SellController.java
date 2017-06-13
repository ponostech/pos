/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers.sales;

import Messages.CustomerMessage;
import Messages.InvoiceMessage;
import com.jfoenix.controls.JFXButton;
import controllers.PonosControllerInterface;
import controllers.customers.CustomerDialog;
import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;
import org.controlsfx.control.MaskerPane;
import org.controlsfx.control.Notifications;
import ponospos.entities.Customer;
import ponospos.entities.Invoice;
import ponospos.entities.InvoiceItem;
import ponospos.entities.Product;
import singletons.Auth;
import singletons.PonosExecutor;
import tasks.customers.FetchAllCustomerTask;
import tasks.invoices.CreateInvoiceTask;
import tasks.products.FindProductStockInStore;
import util.controls.PrintReport;

/**
 * FXML Controller class
 *
 * @author Sawmtea
 */
public class SellController extends AnchorPane implements 
        CustomerDialog.CustomerDialogListener,
        CustomerSelectDialog.CustomerSelectionInterface, 
        QuantityDialog.QuantityListener,
        ProductContainer.ProductContainerListener,
        CheckoutController.CheckoutListener,
        PaymentConfirmDialog.PaymentListener,
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
    private VBox checkoutContainer;
    
    private CheckoutController checkoutController;
    private ObservableList customers=FXCollections.observableArrayList();
    private ObservableList<InvoiceItem> invoiceItems=FXCollections.observableArrayList();
    public SellController(MaskerPane mask,StackPane root){
        try {
            this.mask=mask;
            this.root=root;
            this.productContainer=new ProductContainer(this);
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
        checkoutController=new CheckoutController(this, root);
       
        checkoutContainer.getChildren().add(checkoutController);
        
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
       
    }

    @Override
    public void controlFocus() {
    }
    
    public void fetchAll(){
        FindProductStockInStore task=new FindProductStockInStore();
        task.setStore(Auth.getInstance().getStore());
        task.setOnSucceeded(e->{
            Set<Product> set = new HashSet<>(task.getValue());
            this.productContainer.populateByTable(set);
        });
        task.setOnFailed(e->task.getException().printStackTrace(System.err));
        //fetch all customer
        mask.visibleProperty().bind(task.runningProperty());
        tasks.customers.FetchAllCustomerTask t2=new FetchAllCustomerTask();
        t2.setOnSucceeded(e->{
            this.customers.clear();
            this.customers.addAll(t2.getValue());
            checkoutController.setCustomers(t2.getValue());
        });
        t2.setOnFailed(e->task.getException().printStackTrace(System.err));
        mask.visibleProperty().bind(t2.runningProperty());
        PonosExecutor.getInstance().getExecutor().submit(task);
        PonosExecutor.getInstance().getExecutor().submit(t2);
        
        checkoutController.selectDiscountRadio();
    }

    @Override
    public void onCreate(Customer customer) {
        tasks.customers.CreateTask task=new tasks.customers.CreateTask();
        task.setOnSucceeded(e->{
            Notifications.create().title(CustomerMessage.CREATE_SUCCESS_TITLE)
                    .text(CustomerMessage.CREATE_SUCCESS_MESSAGE)
                    .showInformation();
            customers.add(task.getValue());
            checkoutController.setCustomers(customers);
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
        checkoutController.setSelectedCustomer(customer);
    }

    @Override
    public void onQuantitySet(Product product, int qty) {
        checkoutController.addItemsInCheckout(product, qty);
    }
    
    

    @Override
    public void onSelect(Product p, int count) {
        QuantityDialog d=new QuantityDialog(this);
        d.setMaxValue(count);
        d.setProduct(p);
        d.show(root);
    }
    


    @Override
    public void onPay(Invoice invoice) {
        PaymentConfirmDialog d=new PaymentConfirmDialog(this);
        d.setInvoice(invoice);
        d.fillField();
        d.show(root);
    }

    @Override
    public void onConfirmPayment(Invoice invoice,boolean print) {
        if (print) {
            tasks.invoices.CreateInvoiceTask task = new CreateInvoiceTask();
            task.setInvoice(invoice);
            task.setOnSucceeded(e -> {
                   PrintReport p=new PrintReport();
                   p.printInvoices(invoice);
                   
                   Notifications.create().title(InvoiceMessage.SUCCESS_TITLE)
                        .text(InvoiceMessage.SUCCESS_MESSAGE).showInformation();
                    checkoutController.reset();
            });
            task.setOnFailed(e -> task.getException().printStackTrace(System.err));
            PonosExecutor.getInstance().getExecutor().submit(task);
        }else{
            tasks.invoices.CreateInvoiceTask task=new CreateInvoiceTask();
            task.setInvoice(invoice);
            task.setOnSucceeded(e->{
               
                Notifications.create().title(InvoiceMessage.SUCCESS_TITLE)
                        .text(InvoiceMessage.SUCCESS_MESSAGE).showInformation();
                checkoutController.reset();
            });
            task.setOnFailed(e->task.getException().printStackTrace(System.err));
            PonosExecutor.getInstance().getExecutor().submit(task);

        }
         fetchAll();
    }
    private static final Logger LOG = Logger.getLogger(SellController.class.getName());

    private void doPrint(Invoice invoice) throws JRException {
        File loc = new File(this.getClass().getResource("/resource/reports/receipt.jrxml").toExternalForm());
        JasperDesign design = JRXmlLoader.load(loc);
        JasperReport jasperReport = JasperCompileManager.compileReport(
                design);

        String discount = NumberFormat.getCurrencyInstance(new Locale("en", "in")).format(invoice.getDiscount().doubleValue());
        String tax = NumberFormat.getCurrencyInstance(new Locale("en", "in")).format(invoice.getTax().doubleValue());
        String amount = NumberFormat.getCurrencyInstance(new Locale("en", "in")).format(invoice.getTotal().doubleValue());

        Map parameters = new HashMap();
        parameters.put("CustomerName", invoice.getCustomer().getFirstName());

        parameters.put("Discount", discount);
        parameters.put("Tax", tax);
        parameters.put("Amount", amount);

        parameters.put("StoreName", discount);
        parameters.put("StoreContact", tax);
        parameters.put("StoreAddress", amount);

        Collection collection = invoice.getInvoiceItem();
        JRDataSource datasource = new JRBeanCollectionDataSource(collection);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, datasource);
        JasperViewer.viewReport(jasperPrint);
    }
          
    
    
}
