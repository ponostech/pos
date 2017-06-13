/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers.stores;

import com.jfoenix.controls.JFXDialog;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
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
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import ponospos.entities.Attribute;
import ponospos.entities.Product;
import ponospos.entities.Stock;
import ponospos.entities.Stores;
import singletons.PonosExecutor;
import tasks.products.FindStockByName;

/**
 * FXML Controller class
 *
 * @author Sawmtea
 */
public class StoreStockController extends JFXDialog  {

    @FXML
    private TableView<Product> stockTable;
    @FXML
    private TableColumn<Product, Integer> idCol;
    @FXML
    private TableColumn<Product, String> nameCol;
    @FXML
    private TableColumn<Product, String> priceCol;
    @FXML
    private TableColumn<Product, Integer> stock;
    @FXML
    private TextField searchField;
    @FXML
    private Button searchBtn;
    @FXML
    private Button closeBtn;
    @FXML
    private Label storeLabel;
    @FXML
    private Label addressLabel;
    @FXML
    private Label contactlabel;
    @FXML
    private Label addedByLabel;

    
    private ObservableList<Product> products=FXCollections.observableArrayList();
    private Stores store;

    public StoreStockController() {
        super();
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(this.getClass().getResource("/views/stores/store_stock.fxml"));
            loader.setController(this);
            Region region = loader.load();
            this.setContent(region);
            this.stockTable.setItems(products);
            
            idCol.setCellValueFactory(e->new SimpleObjectProperty<>(e.getValue().getId()));
            nameCol.setCellValueFactory(e->{
                Product p = e.getValue();
                String name=p.getName();
                List<Attribute> atts = p.getAttributes();
                for (Attribute att : atts) {
                    name+=" "+att.getValue();
                }
                return new SimpleStringProperty(name);
            });
            priceCol.setCellValueFactory((TableColumn.CellDataFeatures<Product, String> e)->{
                Product p = e.getValue();
                String str = NumberFormat.getCurrencyInstance(new Locale("en","in")).format(p.getSellingPrice().doubleValue());
                return new SimpleStringProperty(str);
            });
            stock.setCellValueFactory(e->{
                Product p = e.getValue();
                int sum=0;
                for (Stock s : p.getStocks()) {
                    sum+=s.getQuantity();
                }
                return new SimpleObjectProperty<>(sum);
            });
            searchField.textProperty().addListener(e->doSearch());
            searchBtn.setOnAction(e->{doSearch();});
        } catch (IOException ex) {
            Logger.getLogger(StoreStockController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    
    @FXML
    private void onCloseBtnClick(ActionEvent event) {
        close();
    }
    
    public void setProducts(Collection<Product>product){
        this.products.clear();
        this.products.addAll(product);
    }
    public void setStore(Stores store){
        this.store=store;
        storeLabel.setText("Store Name : "+store.getName());
        addressLabel.setText("Address : "+store.getAddress());
        contactlabel.setText("Contact : "+store.getContact());
//        addedByLabel.setText(store.get);
    }
    private void doSearch(){
        String txt=searchField.getText().trim();
        FindStockByName task=new FindStockByName();
        task.setOnSucceeded(e->{
            Set<Product> value =new HashSet<>(task.getValue());
            products.clear();
            
            products.addAll(value);
            stockTable.refresh();
        });
        task.name=txt;
        task.store=this.store;
        PonosExecutor.getInstance().getExecutor().submit(task);
        
    }
    
}
