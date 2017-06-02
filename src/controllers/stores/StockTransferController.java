/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers.stores;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import controllers.PonosControllerInterface;
import controllers.modals.ExceptionDialog;
import controllers.sales.ProductContainer;
import controllers.users.UsersController;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.controlsfx.control.MaskerPane;
import ponospos.entities.Product;
import ponospos.entities.Stores;
import singletons.PonosExecutor;
import tasks.products.FetchAllTask;

/**
 * FXML Controller class
 *
 * @author Sawmtea
 */
public class StockTransferController extends AnchorPane implements
        PonosControllerInterface,
        ProductContainer.ProductContainerListener{

    @FXML
    private VBox productContainer;
    @FXML
    private TableView<?> itemTable;
    @FXML
    private TableColumn<?, ?> items;
    @FXML
    private TableColumn<?, ?> qtyCol;
    @FXML
    private TableColumn<?, ?> actionCol;
    @FXML
    private JFXComboBox<Stores> storeCombo;
    @FXML
    private JFXButton transferBtn;

    private MaskerPane mask;
    private StackPane root;
    
    private ProductContainer productList;
    private ObservableList<Stores> stores=FXCollections.observableArrayList();
//    private ObservableList<Product> products=FXCollections.observableArrayList();
    
    public StockTransferController(MaskerPane mask,StackPane root) {
        this.root=root;
        this.mask=mask;
        try {
            this.mask = mask;
            this.root = root;
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(this.getClass().getResource("/views/stores/stock_transfer.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
            
            this.storeCombo.setItems(stores);
        } catch (IOException ex) {
            Logger.getLogger(UsersController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    
  
    @Override
    public void initDependencies() {
        productList=new ProductContainer(this);
        productContainer.getChildren().add(productList);
    }

    @Override
    public void initControls() {
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

    @Override
    public void onSelect(Product p, int count) {
        
    }
    public void fetchAll(){
        FetchAllTask task=new FetchAllTask();
        task.setOnSucceeded(e->{
            Set<Product>data=new HashSet<>(task.getValue());
            productList.populateByTable(data);
        });
        task.setOnFailed(e->new ExceptionDialog(task.getException()));
        
        tasks.stores.FetchAllTask task1 = new tasks.stores.FetchAllTask();
        task1.setOnSucceeded(e -> {
            stores.clear();
            stores.addAll(task1.getValue());
        });
        task1.setOnFailed(e -> new ExceptionDialog(task.getException()));
        PonosExecutor.getInstance().getExecutor().submit(task);
    }
}
