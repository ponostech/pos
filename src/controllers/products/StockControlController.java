/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers.products;

import Messages.ProductMessage;
import Messages.StockControlMessage;
import com.jfoenix.controls.JFXButton;
import controllers.PonosControllerInterface;
import controllers.modals.ExceptionDialog;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.io.IOException;
import java.util.List;
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
import org.controlsfx.control.MaskerPane;
import org.controlsfx.control.Notifications;
import ponospos.entities.Attribute;
import ponospos.entities.Category;
import ponospos.entities.Product;
import ponospos.entities.Stock;
import ponospos.entities.Stores;
import ponospos.entities.Supplier;
import singletons.PonosExecutor;
import tasks.products.CreateTask;
import tasks.stocks.CreateStockTask;
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
    private TableView<Product> stockTable;
    @FXML
    private TableColumn<Product, Integer> idCol;
    @FXML
    private TableColumn<Product, String> productCol;
    @FXML
    private TableColumn<Product, String> barcodeCol;
    @FXML
    private TableColumn<Product, Category> categoryCol;
    @FXML
    private TableColumn<Product, Integer> qtyCol;
    
    @FXML
    private TableColumn<Product, Product> actionCols;
    @FXML
    private TableView<Product> stockTable1;
    @FXML
    private TableColumn<Product, Integer> idCol1;
    @FXML
    private TableColumn<Product, String> productCol1;
    @FXML
    private TableColumn<Product, String> barcodeCol1;
    @FXML
    private TableColumn<Product, Category> categoryCol1;
    @FXML
    private TableColumn<Product, Integer> qtyCol1;
    
    @FXML
    private TableColumn<Product, Product> actionCols1;
    
    
    @FXML
    private JFXButton addBtn;
    @FXML
    private TextField searchField;
    @FXML
    private Button searchBtn;
    
    private ObservableList<Stock> stocks=FXCollections.observableArrayList();
    private ObservableList<Category> categories=FXCollections.observableArrayList();
    private ObservableList<Supplier>suppliers=FXCollections.observableArrayList();
    private ObservableList<Product>activeProducts=FXCollections.observableArrayList();
    private ObservableList<Product>inactiveProducts=FXCollections.observableArrayList();
    private ObservableList<Stores>stores=FXCollections.observableArrayList();
    
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
        stockTable.setItems(activeProducts);
        stockTable1.setItems(inactiveProducts);
        idCol.setCellValueFactory(e->new SimpleObjectProperty<>(e.getValue().getId()));
        productCol.setCellValueFactory((TableColumn.CellDataFeatures<Product, String> e)->{
            Product p=e.getValue();
            List<Attribute> attr = p.getAttributes();
            String fullname=p.getName();
            if (!attr.isEmpty()) {
                for (Attribute at : attr) {
                    fullname+=" /"+at.getName() +" : "+at.getValue();
                }
            }
            return new SimpleStringProperty(fullname);
        });
        barcodeCol.setCellValueFactory(e->new SimpleStringProperty(e.getValue().getBarcode()));
        categoryCol.setCellValueFactory(e->new SimpleObjectProperty<>(e.getValue().getCategory()));
        qtyCol.setCellValueFactory(e->{
            Product p=e.getValue();
            int sum=0;
            for (Stock stock : p.getStocks()) {
                sum+=stock.getQuantity();
            }
          return new SimpleObjectProperty<>(sum);
        });
        actionCols.setCellFactory((TableColumn<Product, Product> param) -> 
            new TableCell<Product,Product>(){
                private Button addBtn;
                private Button viewBtn;
                
                @Override
            protected void updateItem(Product item, boolean empty) {
                super.updateItem(item, empty); 
                if (empty) {
                    setText(null);
                    setGraphic(null);
                }else{
                    HBox hb=new HBox(5);
                    FontAwesomeIconView addicon=new FontAwesomeIconView(FontAwesomeIcon.PLUS_CIRCLE);
                    FontAwesomeIconView viewIcon=new FontAwesomeIconView(FontAwesomeIcon.EYE);
                    
                    addicon.setFill(Paint.valueOf("#1268b9"));
                    viewIcon.setFill(Paint.valueOf("#1268b9"));
                    
                    addBtn=new Button("",addicon);
                    viewBtn=new Button("",viewIcon);
                    
                    hb.getChildren().addAll(viewBtn,addBtn);
                    
                    setGraphic(hb);
                    viewBtn.setOnAction(e->{
                        StockDetailDialog d=new StockDetailDialog();
                        d.setProduct(activeProducts.get(getIndex()));
                        d.populateData();
                        d.show(root);
                    });
                    addBtn.setOnAction(e->{
                        AddStockDialog d=new AddStockDialog(StockControlController.this);
                        d.setProduct(activeProducts.get(getIndex()));
                        d.setStores(stores);
                        d.show(root);
                    });
                }
            }
                    
                }
        );
        idCol1.setCellValueFactory(e->new SimpleObjectProperty<>(e.getValue().getId()));
        productCol1.setCellValueFactory(e->new SimpleObjectProperty<>(e.getValue().getName()));
        barcodeCol1.setCellValueFactory(e->new SimpleStringProperty(e.getValue().getBarcode()));
        categoryCol1.setCellValueFactory(e->new SimpleObjectProperty<>(e.getValue().getCategory()));
        qtyCol1.setCellValueFactory(e->{
            Product p=e.getValue();
            int sum=0;
            for (Stock stock : p.getStocks()) {
                sum+=stock.getQuantity();
            }
          return new SimpleObjectProperty<>(sum);
        });
        actionCols1.setCellFactory((TableColumn<Product, Product> param) -> 
            new TableCell<Product,Product>(){
                private Button addBtn;
                private Button viewBtn;
                
                @Override
            protected void updateItem(Product item, boolean empty) {
                super.updateItem(item, empty); 
                if (empty) {
                    setText(null);
                    setGraphic(null);
                }else{
                    HBox hb=new HBox(5);
                    FontAwesomeIconView addicon=new FontAwesomeIconView(FontAwesomeIcon.PLUS_CIRCLE);
                    FontAwesomeIconView viewIcon=new FontAwesomeIconView(FontAwesomeIcon.EYE);
                    
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
                        d.setProduct(inactiveProducts.get(getIndex()));
                        d.setStores(stores);
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
        searchBtn.setOnAction(e->fetchStock());
        searchField.textProperty().addListener(inv->doSearch());
        
        addBtn.setOnAction(e->{
            ProductDialog d=new ProductDialog(this);
            d.setCategories(categories);
            d.setSuppliers(suppliers);
            d.setStores(stores);
            d.isCreate();
            d.show(root);
        });
    }

    @Override
    public void controlFocus() {
    }

    public void fetchStock() {
//        FetchAllStockTask task=new FetchAllStockTask();
//        task.setOnSucceeded(e->{
//            stocks.clear();
//            stocks.addAll(task.getValue());
//        });
//        mask.visibleProperty().bind(task.runningProperty());
//        task.setOnFailed(e->task.getException().printStackTrace(System.err));
//        
        tasks.categories.FetchAllTask t2=new tasks.categories.FetchAllTask();
        t2.setOnSucceeded(e->{
            categories.clear();
            categories.addAll(t2.getValue());
        });
        t2.setOnFailed(e->{
            ExceptionDialog d=new ExceptionDialog(t2.getException());
            d.show(root);
        });
        mask.visibleProperty().bind(t2.runningProperty());
        
        tasks.suppliers.FetchAllTask t4=new tasks.suppliers.FetchAllTask();
        t4.setOnSucceeded(e->{
            suppliers.clear();
            suppliers.addAll(t4.getValue());
        });
        t4.setOnFailed(e -> t4.getException().printStackTrace(System.err));

        tasks.stores.FetchAllTask t5=new tasks.stores.FetchAllTask();
        t5.setOnSucceeded(e->{
            stores.clear();
            stores.addAll(t5.getValue());
        });
        t5.setOnFailed(e -> t5.getException().printStackTrace(System.err));
        tasks.products.FetchAllTask t6=new tasks.products.FetchAllTask();
        t6.setOnSucceeded(e->{
            
            activeProducts.clear();
            inactiveProducts.clear();
            for (Product p : t6.getValue()) {
                if (p.isActive()) {
                    
                    activeProducts.add(p);
                }else{
                    inactiveProducts.add(p);
                }
            }
            stockTable.refresh();
            stockTable1.refresh();
        });
        t6.setOnFailed(e -> t6.getException().printStackTrace(System.err));

//        PonosExecutor.getInstance().getExecutor().submit(task);
        PonosExecutor.getInstance().getExecutor().submit(t2);
        PonosExecutor.getInstance().getExecutor().submit(t4);
        PonosExecutor.getInstance().getExecutor().submit(t5);
        PonosExecutor.getInstance().getExecutor().submit(t6);
        
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
        tasks.products.CreateTask task=new CreateTask();
        task.setOnSucceeded(e->{
            Notifications.create()
                .title(ProductMessage.CREATE_SUCCESS_TITLE)
                .text(ProductMessage.CREATE_SUCCESS_MESSAGE)
                .showInformation();
            if (task.getValue().isActive()) {
                activeProducts.add(task.getValue());
            }else{
                inactiveProducts.add(task.getValue());
            }
        });
        task.setOnFailed(e->task.getException().printStackTrace(System.err));
        mask.visibleProperty().bind(task.runningProperty());
        task.setProduct(product);
        PonosExecutor.getInstance().getExecutor().submit(task);
    }

    @Override
    public void onUpdate(Product product) {
    }

    @Override
    public void onUpdate(Stock stock) {
        CreateStockTask task=new CreateStockTask();
        task.setStock(stock);
        task.setOnSucceeded(e->{
            Notifications.create().title(StockControlMessage.STOCK_CREATE_TITLE).text(StockControlMessage.STOCK_CREATE_SUCCESS)
                    .showInformation();
            stockTable.refresh();
            stockTable1.refresh();
            System.out.println("add stock task is finish"+task.isDone());
     
        });
        task.setOnFailed(e->{
//            ExceptionDialog d=new ExceptionDialog(task.getException());
//            d.show(root);
            task.getException().printStackTrace(System.err);
        });
        PonosExecutor.getInstance().getExecutor().submit(task);
    }
}
