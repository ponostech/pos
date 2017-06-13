/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers.sales;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.controlsfx.control.Notifications;
import ponospos.entities.Attribute;
import ponospos.entities.Product;
import ponospos.entities.Stock;
import ponospos.entities.Stores;
import singletons.Auth;
import singletons.PonosExecutor;
import tasks.products.FindAvailableStock;

/**
 *
 * @author Sawmtea
 */
public class ProductContainer extends AnchorPane {

    
    public interface ProductContainerListener{
        public void onSelect(Product p,int count);
    }
    private ObservableList<Product> products=FXCollections.observableArrayList();
    private ObservableList<Node> nodes=FXCollections.observableArrayList();
    
    @FXML
    private TextField searchField;
    private GridPane gridPane;
    private  ListView<Node> listView;
    
    @FXML
    private TableView<Product> itemsTable;
    @FXML
    private TableColumn<Product,String> nameCol;
    @FXML
    private TableColumn<Product,String> barcodeCol;
    @FXML
    private TableColumn<Product,Integer> qtyCol;
    @FXML
    private TableColumn<Product,Product>actionCol;

    
    private ProductContainerListener listener;
    private final int NO_OF_COLS=5;
    
    public ProductContainer(ProductContainerListener listener){
        super();
        try {
            this.listener=listener;
            FXMLLoader loader=new FXMLLoader();
            loader.setLocation(this.getClass().getResource("/views/sales/product_container.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
//        this.setPadding(new Insets(10));
//        this.setSpacing(20);
//        this.setPrefWidth(450);
//        this.setEffect(new DropShadow());
//        this.setAlignment(Pos.TOP_CENTER);
//        
//        this.searchField=new TextField();
//        this.searchField.setPrefHeight(50);
//        
//        this.searchField.setFont(new Font(18));
//        
//        this.searchField.setPromptText("Type product name or scan barcode");
//        
//        this.gridPane=new GridPane();
//        this.gridPane.setHgap(10);
//        this.gridPane.setVgap(10);
//        
//        listView=new ListView<>();
//        listView.setItems(nodes);
//        
//        this.getChildren().addAll(searchField,new Separator(),listView);
//
        this.itemsTable.setItems(products);
        this.nameCol.setCellValueFactory(e->{
            String attrs="";
            if (!e.getValue().getAttributes().isEmpty()) {
                for (Attribute attr : e.getValue().getAttributes()) {
                    attrs+= " > " +attr.getValue();
                }
            }
                Product value = e.getValue();
                String name=value.getName() + "\n"+attrs;
            return new SimpleStringProperty(name);
        });
        this.barcodeCol.setCellValueFactory(e->new SimpleStringProperty(e.getValue().getBarcode()));
        this.qtyCol.setCellValueFactory(e->{
            Product p=e.getValue();
            int sum=0;
            for (Stock stock : p.getStocks()) {
                sum+=stock.getQuantity();
            }
            return new SimpleObjectProperty(sum);
        });
        this.actionCol.setCellFactory((TableColumn<Product, Product> param) -> new TableCell<Product,Product>(){
                @Override
                protected void updateItem(Product item, boolean empty) {
                    super.updateItem(item, empty); 
                    if (empty) {
                        setText(null);
                        setGraphic(null);
                    }else{
                        FontAwesomeIconView icon=new FontAwesomeIconView(FontAwesomeIcon.PLUS_CIRCLE);
                        icon.setFill(Color.CORAL);
                        icon.setSize("14px");
                        Button btn=new Button("",icon);
                        setGraphic(btn);
                        btn.setOnAction(e->{
                            Product p = products.get(getIndex());
                            if (getMaxStock(p)<=0) {
                                Notifications.create().text("Out of stock").title("Stock info").showError();
                            }else{
                                listener.onSelect(p, getMaxStock(p));
                            }
                        });
                    }
                }
            
        });
        
        
            this.searchField.setOnKeyReleased(e->{
                doSearch();
            });
        } catch (IOException ex) {
            Logger.getLogger(ProductContainer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    
    public void populateByTable(Set<Product>data){
        //check data 
        for (Product p : data) {
            System.out.println("products "+p);
            System.out.println("Stock size "+p.getStocks().size());
            System.out.println("");
        }
        products.clear();
        products.addAll(data);
        itemsTable.refresh();
    }
    
    public void populateData1(Set<Product> data){
        products.clear();
        products.addAll(data);
        nodes.clear();
        for (Product p : data) {
            if (getMaxStock(p)>0) {
                nodes.add(wrapIntoNode(p));
            }
        }
        
        
    }
    public void populateData(List<Product> data){
        this.products.clear();
//        this.gridPane.getChildren().clear();
        for (Product p : data) {
            if (checkStockInStore(p,Auth.getInstance().getStore())) {
                products.add(p);
            }
        }
        int col=0;
        int row=0;
        for (int i = 0; i < products.size(); i++) {
            if (col==NO_OF_COLS) {
                col=0;
                row++;
            }
            gridPane.add(wrapIntoPane(products.get(i)),col,row);
            
            col++;
        }
    }
    
    public Product getSelectedProduct(){
        return null;
    }
    private Node wrapIntoNode(Product p){
        HBox hb=new HBox(5);
        hb.setPadding(new Insets(10));
        hb.setMaxSize(Double.MAX_VALUE, 60);
        hb.setEffect(new DropShadow());
        
        FontAwesomeIconView infoIcon=new FontAwesomeIconView(FontAwesomeIcon.TAG);
        infoIcon.setFill(Color.AZURE);
        Label nameLabel=new Label(p.getName(),infoIcon);
        Label barLabel=new Label(p.getBarcode());
        VBox infoBox=new VBox(4);
        infoBox.getChildren().addAll(nameLabel,barLabel);
        
        FontAwesomeIconView addIcon = new FontAwesomeIconView(FontAwesomeIcon.PLUS_CIRCLE);
        addIcon.setFill(Color.GREENYELLOW);
        Label qtyLabel=new Label("Qty: "+getMaxStock(p));
        Button addBtn=new Button("",addIcon);
        VBox controlBox = new VBox(4);
        controlBox.getChildren().addAll(qtyLabel,addBtn);
        
        addBtn.setOnMouseClicked(e->{
            e.consume();
            listener.onSelect(p, getCount(p));
        });
        
        Pane gap=new Pane();
        gap.setMaxSize(Double.MAX_VALUE, 90);
        HBox.setHgrow(gap, Priority.ALWAYS);
        hb.getChildren().addAll(infoBox,gap,controlBox);
        
        return hb;
    }

    private Node wrapIntoPane(Product product) {
        VBox pane=new VBox(5);
        pane.getStyleClass().add("box");
        pane.setPrefSize(70, 70);
        pane.setPadding(new Insets(10));
        pane.setEffect(new DropShadow());
        pane.setAlignment(Pos.CENTER);
        pane.setFocusTraversable(true);
        
        Label countLabel=new Label();
        countLabel.getStyleClass().add("badge");
        
        countLabel.setText(Integer.toString(getCount(product)));
        
        Label nameLabel=new Label();
        nameLabel.setWrapText(true);
        nameLabel.setText(product.getName());
        countLabel.getStyleClass().add("h4");
        
        Label variantLabel=new Label();
        variantLabel.setWrapText(true);
        variantLabel.setText(generateVariantname(product));
        variantLabel.getStyleClass().add("h5");

        pane.getChildren().addAll(countLabel,nameLabel,variantLabel);
        pane.setOnMouseClicked(e->{
            listener.onSelect(product, getCount(product));
            e.consume();
        });
        return pane;
        
    }
    private int getCount(Product p){
        int sum=0;
        for (Stock stock : p.getStocks()) {
            if (stock.getStore().equals(Auth.getInstance().getStore())) {
                sum += stock.getQuantity();
            }
        }
        return sum;
    }

    private String generateVariantname(Product product) {
        String text="[";
        for (Attribute attr : product.getAttributes()) {
            text+=attr.getName()+" : "+attr.getValue()+ " :: ";
        }
        return text+"]";
    }

    private void doSearch() {
        String text=searchField.getText().trim();
        FindAvailableStock task=new FindAvailableStock();
        task.setParam(text);
        task.setStore(Auth.getInstance().getStore());
        task.setOnSucceeded(e->{
            Set<Product> set=new HashSet<>(task.getValue());
            populateByTable(set);
        });
        task.setOnFailed(e->task.getException().printStackTrace(System.err));
        PonosExecutor.getInstance().getExecutor().submit(task);
    }
    public int getMaxStock(Product product){
        int sum=0;
        for (Stock stock : product.getStocks()) {
            if (stock.getStore().equals(Auth.getInstance().getStore())) {
                sum += stock.getQuantity();
            }
        }
        return sum;
    }
    private boolean checkStockInStore(Product p, Stores store) {
        if (p.getStocks().stream().anyMatch((s) -> (s.getStore().equals(store)))) {
            return true;
        }
        return false;
    }
    
}
