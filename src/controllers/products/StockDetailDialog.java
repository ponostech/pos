/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers.products;

import com.jfoenix.controls.JFXDialog;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import ponospos.entities.Attribute;
import ponospos.entities.Invoice;
import ponospos.entities.Product;
import ponospos.entities.Stock;
import ponospos.entities.User;
import util.TransactionType;

/**
 * FXML Controller class
 *
 * @author Sawmtea
 */
public class StockDetailDialog extends JFXDialog {

    @FXML
    private TableView<Stock> stockTable;
    @FXML
    private TableColumn<Stock, String> dateCol;
    @FXML
    private TableColumn<Stock, User> employeeCol;
    @FXML
    private TableColumn<Stock, Node> inOutCol;
    @FXML
    private TableColumn<Stock, Integer> qtyCol;
    @FXML
    private TableColumn<Stock, String> transactionCol;
    @FXML
    private TableColumn<Stock, Invoice> invoiceCol;
    @FXML
    private TableColumn<Stock, String> remarkCol;
    @FXML
    private Label idLabel;
    @FXML
    private Label nameLabel;
    @FXML
    private Label variant;
    @FXML
    private Label stockLabel;
    @FXML
    private MaterialDesignIconView close;
    
    private Product product;

    ObservableList<Stock> stocks = FXCollections.observableArrayList();

    public StockDetailDialog() {
        super();
        try {
            FXMLLoader loader=new FXMLLoader();
            loader.setLocation(this.getClass().getResource("/views/products/stock_details_dialog.fxml"));
            loader.setController(this);
            Region region = loader.load();
            this.setContent(region);
            close.setOnMouseClicked(e->StockDetailDialog.this.close());
            this.setOnDialogOpened(e->close.requestFocus());
        } catch (IOException ex) {
            Logger.getLogger(StockDetailDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setProduct(Product p){
        this.product=p;
        stocks.clear();
        stocks.addAll(p.getStocks());
    }
    
    public void populateData(){
        this.idLabel.setText(Integer.toString(product.getId()));
        this.nameLabel.setText(product.getName());
        String str="";
        for (Attribute attr : product.getAttributes()) {
            str+=" || " +attr.getName()+" : "+attr.getValue();
        }
        this.variant.setText(str);
        this.stockLabel.setText(getStockCount(product));
        this.stockTable.setItems(stocks);
        dateCol.setCellValueFactory(e->{
            Stock stock=e.getValue();
            Date date = stock.getCreatedAt();
            SimpleDateFormat fm=new SimpleDateFormat("dd/MM/yy :hh:mm:ss");
            return new SimpleStringProperty(fm.format(date));
        });
        employeeCol.setCellValueFactory(e->new SimpleObjectProperty<>(e.getValue().getUser()));
        inOutCol.setCellFactory((TableColumn<Stock, Node> param) -> 
                new TableCell(){
            @Override
            protected void updateItem(Object item, boolean empty) {
                if (empty) {
                    setText(null);
                    setGraphic(null);
                }else{
                    FontAwesomeIconView upIcon=new FontAwesomeIconView(FontAwesomeIcon.ARROW_CIRCLE_UP);
                    FontAwesomeIconView downIcon=new FontAwesomeIconView(FontAwesomeIcon.ARROW_CIRCLE_DOWN);
                    
                    upIcon.setFill(Color.BLUE);
                    boolean isIN=stocks.get(getIndex()).getTransactionType().equalsIgnoreCase(TransactionType.STOCK_UPDATE.toString());
                    if (isIN) {
                        setGraphic(upIcon);
                    }else{
                        setGraphic(downIcon);
                    }
                }
            }
                    
                }
        );
        qtyCol.setCellValueFactory(e->new SimpleObjectProperty<>(e.getValue().getQuantity()));
        transactionCol.setCellValueFactory(e->new SimpleStringProperty(e.getValue().getTransactionType()));
        invoiceCol.setCellValueFactory(e->new SimpleObjectProperty<>(e.getValue().getInvoice()));
        remarkCol.setCellValueFactory(e->new SimpleStringProperty(e.getValue().getRemark()));
    }

    private String getStockCount(Product product) {
        int count=0;
        for (Stock stock : product.getStocks()) {
            count+=stock.getQuantity();
        }
        String format = NumberFormat.getInstance(new Locale("en","in")).format(count);
        return format;
    }
    
}
