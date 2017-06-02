/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers.sales;

import controllers.PonosControllerInterface;
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
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import org.controlsfx.control.MaskerPane;
import ponospos.entities.Customer;
import ponospos.entities.Invoice;
import ponospos.entities.InvoiceItem;
import ponospos.entities.User;
import singletons.PonosExecutor;
import tasks.invoices.FetchAllInvoiceTask;
import tasks.invoices.FindInvoiceBetweenDateTask;
import util.DateConverter;

/**
 * FXML Controller class
 *
 * @author Sawmtea
 */
public class SaleHistoryController extends AnchorPane 
implements PonosControllerInterface{

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
    private DatePicker fromDatePicker;
    @FXML
    private DatePicker toDatePicker;
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
            SimpleDateFormat fmt=new SimpleDateFormat("dd/MM/yy hh:mm:ss:");
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

            @Override
            protected void updateItem(Invoice item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    HBox hb = new HBox(5);
                    FontAwesomeIconView addicon = new FontAwesomeIconView(FontAwesomeIcon.EDIT);
                    FontAwesomeIconView viewIcon = new FontAwesomeIconView(FontAwesomeIcon.EYE);

                    addicon.setFill(Paint.valueOf("#1268b9"));
                    viewIcon.setFill(Paint.valueOf("#1268b9"));

                    editBtn = new Button("", addicon);
                    viewBtn = new Button("", viewIcon);

                    hb.getChildren().addAll(viewBtn, editBtn);

                    setGraphic(hb);
                    viewBtn.setOnAction(e -> {
                       SalesDetailDialog d=new SalesDetailDialog();
                       d.setInvoice(invoices.get(getIndex()));
                       d.show(root);
                    });
                    editBtn.setOnAction(e -> {
                       
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
       
    
}
