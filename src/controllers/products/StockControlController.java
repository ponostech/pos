/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers.products;

import com.jfoenix.controls.JFXButton;
import controllers.PonosControllerInterface;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.awt.Color;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import org.controlsfx.control.MaskerPane;
import ponospos.entities.Category;
import ponospos.entities.Invoice;
import ponospos.entities.Product;
import ponospos.entities.Stock;
import singletons.PonosExecutor;
import tasks.stocks.FetchAllStockTask;
import tasks.stocks.FindStockByProductNameTask;

/**
 * FXML Controller class
 *
 * @author Sawmtea
 */
public class StockControlController extends AnchorPane implements
        AddStockDialog.StockListener,
        PonosControllerInterface,
        ProductDialog.ProductDialogListener{

   
    private StackPane root;
    private MaskerPane mask;
 
    @FXML
    private TableView<Stock> stockTable;
    @FXML
    private TableColumn<Stock, Integer> idCol;
    @FXML
    private TableColumn<Stock, Product> productCol;
    @FXML
    private TableColumn<Stock, String> barcodeCol;
    @FXML
    private TableColumn<Stock, Category> categoryCol;
    @FXML
    private TableColumn<Stock, Integer> qtyCol;
    @FXML
    private TableColumn<Stock, Invoice> invoiceCol;
    @FXML
    private TableColumn<Stock, Stock> actionCols;
    @FXML
    private JFXButton addBtn;
    @FXML
    private TextField searchField;
    @FXML
    private Button searchBtn;
    
    private ObservableList<Stock> stocks=FXCollections.observableArrayList();
    public StockControlController(MaskerPane mask,StackPane root){
        try {
            this.mask=mask;
            this.root=root;
            FXMLLoader loader=new FXMLLoader();
            loader.setLocation(this.getClass().getResource("/views/products/stock_control.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
            } catch (IOException ex) {
            Logger.getLogger(StockControlController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void initDependencies() {
    }

    @Override
    public void initControls() {
        stockTable.setItems(stocks);
        idCol.setCellValueFactory(e->new SimpleObjectProperty<>(e.getValue().getId()));
        productCol.setCellValueFactory(e->new SimpleObjectProperty<>(e.getValue().getProduct()));
        barcodeCol.setCellValueFactory(e->new SimpleStringProperty(e.getValue().getProduct().getBarcode()));
        categoryCol.setCellValueFactory(e->new SimpleObjectProperty<>(e.getValue().getProduct().getCategory()));
        qtyCol.setCellValueFactory(e->new SimpleObjectProperty<>(e.getValue().getQuantity()));
        invoiceCol.setCellValueFactory(e->new SimpleObjectProperty<>(e.getValue().getInvoice()));
        actionCols.setCellFactory((TableColumn<Stock, Stock> param) -> 
            new TableCell<Stock,Stock>(){
                private Button addBtn;
                private Button viewBtn;
                
                @Override
            protected void updateItem(Stock item, boolean empty) {
                super.updateItem(item, empty); 
                if (empty) {
                    setText(null);
                    setGraphic(null);
                }else{
                    HBox hb=new HBox(5);
                    FontAwesomeIconView addicon=new FontAwesomeIconView(FontAwesomeIcon.PLUS_CIRCLE);
                    FontAwesomeIconView viewIcon=new FontAwesomeIconView(FontAwesomeIcon.EYE);
                    
                    addicon.setFont(new Font(14));
                    viewIcon.setFont(new Font(14));
                    
                    addicon.setFill(Paint.valueOf("#1268b9"));
                    viewIcon.setFill(Paint.valueOf("#1268b9"));
                    
                    addBtn=new Button("",addicon);
                    viewBtn=new Button("",viewIcon);
                    
                    hb.getChildren().addAll(viewBtn,addBtn);
                    
                    setGraphic(hb);
                    viewBtn.setOnAction(e->{
                        //TODO :: to nothing
                    });
                    addBtn.setOnAction(e->{
                        AddStockDialog d=new AddStockDialog(StockControlController.this);
                        d.show(root);
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
        searchBtn.setOnAction(e->doSearch());
        searchField.textProperty().addListener(inv->doSearch());
        
        addBtn.setOnAction(e->{
            ProductDialog d=new ProductDialog(this);
            d.isCreate();
            d.show(root);
        });
    }

    @Override
    public void controlFocus() {
    }

    public void fetchStock() {
        FetchAllStockTask task=new FetchAllStockTask();
        task.setOnSucceeded(e->{
            stocks.clear();
            stocks.addAll(task.getValue());
        });
        mask.visibleProperty().bind(task.runningProperty());
        task.setOnFailed(e->task.getException().printStackTrace(System.err));
        PonosExecutor.getInstance().getExecutor().submit(task);
        
    }

    private void doSearch() {
        String text=searchField.getText().trim();
        FindStockByProductNameTask ftask=new FindStockByProductNameTask();
        ftask.setOnSucceeded(e->{
            stocks.clear();
            stocks.addAll(ftask.getValue());
        });
        ftask.setParam(text);
        ftask.setOnFailed(e->ftask.getException().printStackTrace(System.err));
        PonosExecutor.getInstance().getExecutor().submit(ftask);
    }

    @Override
    public void onCreate(Product product) {
    }

    @Override
    public void onUpdate(Product product) {
    }

    @Override
    public void onUpdate(Stock stock) {
        
    }
}
