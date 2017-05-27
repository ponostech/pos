/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers.sales;

import com.jfoenix.controls.JFXRippler;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import ponospos.entities.Attribute;
import ponospos.entities.Product;
import ponospos.entities.Stock;
import singletons.PonosExecutor;
import tasks.products.FindProductTask;

/**
 *
 * @author Sawmtea
 */
public class ProductContainer extends VBox{
    private ObservableList<Product> products=FXCollections.observableArrayList();
    private TextField searchField;
    private GridPane gridPane;
    
    private final int NO_OF_COLS=4;
    
    public ProductContainer(){
        super();
        this.setPadding(new Insets(10));
        this.setSpacing(20);
        this.setPrefWidth(450);
        this.setEffect(new DropShadow());
        this.setAlignment(Pos.TOP_CENTER);
        
        this.searchField=new TextField();
        this.searchField.setPrefHeight(50);
        
        this.searchField.setFont(new Font(18));
        
        this.searchField.setPromptText("Type product name or scan barcode");
        
        this.gridPane=new GridPane();
        this.gridPane.setHgap(10);
        this.gridPane.setVgap(10);
        this.getChildren().addAll(searchField,gridPane);
        
        this.searchField.setOnKeyReleased(e->{
            doSearch();
        });
        
    }
    
    public void populateData(List<Product> data){
        this.products.clear();
        this.gridPane.getChildren().clear();
        data.stream().filter((product) -> (product.isActive())).forEachOrdered((product) -> {
            products.add(product);
        });
        int col=0;
        int row=0;
        for (int i = 0; i < products.size(); i++) {
            if (i==NO_OF_COLS) {
                col++;
                row=0;
            }
            gridPane.add(wrapIntoPane(products.get(i)),row,col);
            row++;
        }
    }
    
    public Product getSelectedProduct(){
        return null;
    }

    private Node wrapIntoPane(Product product) {
        VBox pane=new VBox(5);
        pane.getStyleClass().add("box");
        pane.setPrefSize(100, 100);
        pane.setPadding(new Insets(15));
        pane.setEffect(new DropShadow());
        pane.setAlignment(Pos.CENTER);
        
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
            JFXRippler rippler = new JFXRippler(pane);
        });
        return pane;
        
    }
    private int getCount(Product p){
        int sum=0;
        for (Stock stock : p.getStocks()) {
            sum+=stock.getQuantity();
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
        FindProductTask task=new FindProductTask();
        task.setParam(text);
        task.setOnSucceeded(e->{
            populateData(task.getValue());
        });
        task.setOnFailed(e->task.getException().printStackTrace(System.err));
        PonosExecutor.getInstance().getExecutor().submit(task);
    }
    
}
