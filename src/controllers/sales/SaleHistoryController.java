/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers.sales;

import Messages.ConfirmationMessage;
import Messages.InvoiceMessage;
import com.jfoenix.controls.JFXDatePicker;
import controllers.PonosControllerInterface;
import controllers.modals.ConfirmDialog;
import controllers.users.UsersController;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
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
import javafx.scene.paint.Paint;
import org.controlsfx.control.MaskerPane;
import org.controlsfx.control.Notifications;
import ponospos.entities.Customer;
import ponospos.entities.Invoice;
import ponospos.entities.InvoiceItem;
import ponospos.entities.User;
import singletons.PonosExecutor;
import tasks.invoices.DeleteInvoiceTask;
import tasks.invoices.FetchAllInvoiceTask;
import tasks.invoices.FindInvoiceBetweenDateTask;
import util.DateConverter;
import util.controls.PrintReport;

/**
 * FXML Controller class
 *
 * @author Sawmtea
 */
public class SaleHistoryController extends AnchorPane 
implements PonosControllerInterface
,ConfirmDialog.ConfirmDialogListener{

    @FXML
    private AnchorPane AnchorPane;
    @FXML
    private TableView<Invoice> invoiceTable;
    @FXML
    private TableColumn<Invoice, Integer> idCol;
    @FXML
    private TableColumn<Invoice, String> dateCol;
    @FXML
    private TableColumn<Invoice, Customer> customerCol;
    @FXML
    private TableColumn<Invoice, User> userCol;
    @FXML
    private TableColumn<Invoice, String> amountCol;
    @FXML
    private TableColumn<Invoice, Integer> itemsCol;
    @FXML
    private TableColumn<Invoice, Invoice> actions;
    @FXML
    private JFXDatePicker fromDatePicker;
    @FXML
    private JFXDatePicker toDatePicker;
    @FXML
    private Button filterBtn;

    
    private StackPane root;
    private MaskerPane mask;
    private ObservableList<Invoice> invoices=FXCollections.observableArrayList();
    public SaleHistoryController(MaskerPane mask,StackPane root) {
        
        try {
            this.mask = mask;
            this.root = root;
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(this.getClass().getResource("/views/sales/sell_history.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        } catch (IOException ex) {
            Logger.getLogger(UsersController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void initDependencies() {
    }

    @Override
    public void initControls() {
        invoiceTable.setItems(invoices);
        idCol.setCellValueFactory(e->new SimpleObjectProperty<>(e.getValue().getId()));
        dateCol.setCellValueFactory(e->{
            Invoice inv = e.getValue();
            SimpleDateFormat fmt=new SimpleDateFormat("EEE dd/MMM/yy hh:mm:ss a");
            String str = fmt.format(inv.getInvoiceDate());
            return new SimpleStringProperty(str);
        });
        customerCol.setCellValueFactory(e->new SimpleObjectProperty<>(e.getValue().getCustomer()));
        userCol.setCellValueFactory(e->new SimpleObjectProperty<>(e.getValue().getSoldBy()));
        itemsCol.setCellValueFactory(e->{
            Invoice inv = e.getValue();
            int sum=0;
            for (InvoiceItem in : inv.getInvoiceItem()) {
                sum+=in.getQuantity();
            }
            return new SimpleObjectProperty<>(sum);
        });
        amountCol.setCellValueFactory(e->{
            String str = NumberFormat.getCurrencyInstance(new Locale("en", "in")).format(e.getValue().getTotal());
            return new SimpleStringProperty(str);
        });
        actions.setCellFactory((TableColumn<Invoice, Invoice> param)
                -> new TableCell<Invoice, Invoice>() {
            private Button editBtn;
            private Button viewBtn;
            private Button printBtn;

            @Override
            protected void updateItem(Invoice item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    HBox hb = new HBox(5);
                    FontAwesomeIconView addicon = new FontAwesomeIconView(FontAwesomeIcon.TRASH);
                    FontAwesomeIconView viewIcon = new FontAwesomeIconView(FontAwesomeIcon.EYE);
                    FontAwesomeIconView printIcon = new FontAwesomeIconView(FontAwesomeIcon.PRINT);

                    addicon.setFill(Paint.valueOf("#1268b9"));
                    viewIcon.setFill(Paint.valueOf("#1268b9"));
                    printIcon.setFill(Paint.valueOf("#1268b9"));

                    editBtn = new Button("", addicon);
                    viewBtn = new Button("", viewIcon);
                    printBtn = new Button("", printIcon);

                    hb.getChildren().addAll(printBtn,viewBtn, editBtn);

                    setGraphic(hb);
                    viewBtn.setOnAction(e -> {
                       SalesDetailDialog d=new SalesDetailDialog();
                       d.setInvoice(invoices.get(getIndex()));
                       d.show(root);
                    });
                    editBtn.setOnAction(e -> {
                        ConfirmDialog dialog = new ConfirmDialog(ConfirmationMessage.TITLE, ConfirmationMessage.MESSAGE);
                        dialog.setListener(SaleHistoryController.this);
                        dialog.setModel(invoices.get(getIndex()));
                        dialog.show(root);
                        e.consume();
                    });
                    printBtn.setOnAction(e -> {
                        PrintReport report=new PrintReport();
                        report.printInvoices(invoices.get(getIndex()));
                    });
                }
            }

        }
        );
    }

    @Override
    public void bindControls() {
    }

    @Override
    public void hookupEvent() {
    }

    @Override
    public void controlFocus() {
    }
    
    public void fetchAll(){
        FetchAllInvoiceTask task=new FetchAllInvoiceTask();
        mask.visibleProperty().bind(task.runningProperty());
        task.setOnSucceeded(ev -> {
            invoices.clear();
            invoices.addAll(task.getValue());
        });
        PonosExecutor.getInstance().getExecutor().submit(task);
    }

    @FXML
    public void onFilter(ActionEvent e){
        LocalDate from = fromDatePicker.getValue();
        LocalDate to = toDatePicker.getValue();
        
        FindInvoiceBetweenDateTask task =new FindInvoiceBetweenDateTask();
        task.setFrom(DateConverter.toUtilDate(from));
        task.setTo(DateConverter.toUtilDate(to));
        
        mask.visibleProperty().bind(task.runningProperty());
        task.setOnSucceeded(ev->{
            invoices.clear();
            invoices.addAll(task.getValue());
        });
        PonosExecutor.getInstance().getExecutor().submit(task);
        
        
    }

    @Override
    public void onClickYes(Object obj) {
        tasks.invoices.DeleteInvoiceTask t=new DeleteInvoiceTask();
        t.setOnSucceeded(e->{
            invoices.remove(t.getValue());
            Notifications.create().title(InvoiceMessage.DELETE_SUCCESS_TITLE).text(InvoiceMessage.DELETE_SUCCESS_MESSAGE).showInformation();
        });
        t.setInvoice((Invoice) obj);
        mask.visibleProperty().bind(t.runningProperty());
        t.setOnFailed(e->t.getException().printStackTrace(System.err));
        
        PonosExecutor.getInstance().getExecutor().submit(t);

    }
       
    
}
