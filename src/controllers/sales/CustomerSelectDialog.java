/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers.sales;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXListView;
import controllers.customers.CustomersController;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import ponospos.entities.Customer;
import singletons.PonosExecutor;
import tasks.customers.FindCustomerTask;

/**
 * FXML Controller class
 *
 * @author Sawmtea
 */
public class CustomerSelectDialog extends JFXDialog {

    
    public interface CustomerSelectionInterface{
        public void onSelect(Customer customer);
    }
    @FXML
    private TextField searchField;
    @FXML
    private JFXListView<Customer> customerList;
    @FXML
    private JFXButton positiveBtn;
    @FXML
    private JFXButton negativeBtn;
    @FXML
    private MaterialDesignIconView close;
    
    private CustomerSelectionInterface listener;
    private ObservableList<Customer> customers=FXCollections.observableArrayList();
    public CustomerSelectDialog(CustomerSelectionInterface listener){
        this.listener=listener;
        try {
            FXMLLoader loader=new FXMLLoader();
            loader.setLocation(this.getClass().getResource("/views/sales/customer_select_dialog.fxml"));
            loader.setController(this);
            Region parent = loader.load();
            this.setContent(parent);
           
            this.setTransitionType(JFXDialog.DialogTransition.TOP);
            
            customerList.setItems(customers);
            close.setOnMouseClicked(e->this.close());
            this.setOnDialogOpened(e->searchField.requestFocus());
            positiveBtn.disableProperty().bind(customerList.getSelectionModel().selectedItemProperty().isNull());
            positiveBtn.setOnAction(e->{
                CustomerSelectDialog.this.close();
                listener.onSelect(customerList.getSelectionModel().getSelectedItem());
                    });
            negativeBtn.setOnAction(e->close());
           
            searchField.setOnKeyReleased(e->{
                String txt = searchField.getText().trim();
                doSearch(txt);
            });
//            customizeListView();
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
            Logger.getLogger(CustomersController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setCustomers(List<Customer> customers) {
        this.customers.clear();
        this.customers.addAll(customers);
    }
    private void customizeListView(){
        this.customerList.setCellFactory(new Callback<ListView<Customer>, ListCell<Customer>>() {
            @Override
            public ListCell<Customer> call(ListView<Customer> param) {
                return new ListCell<Customer>(){
                    @Override
                    protected void updateItem(Customer item, boolean empty) {
                        super.updateItem(item, empty); 
                        if (empty) {
                            setText(null);
                            setGraphic(null);
                        }else{
                            HBox hb=new HBox(5);
                            hb.setPadding(new Insets(10));
                            
                            FontAwesomeIconView icon=new FontAwesomeIconView(FontAwesomeIcon.USER_TIMES);
                            icon.setSize("28px");
                            icon.setFill(Color.GRAY);
                            
                            Label title=new Label(customers.get(getIndex()).toString());
//                            title.getStyleClass().add("list-item");
                            
                            Label sub_title = new Label(customers.get(getIndex()).getContact());
                            title.getStyleClass().add("list-item");
                            
                            Pane pane=new Pane();
                            pane.setMaxWidth(Double.MAX_VALUE);
                            HBox.setHgrow(pane, Priority.ALWAYS);
                            hb.getChildren().addAll(icon,title,pane,sub_title);
                            
                        }
                    }
                    
                };
            }
        });
    }
    
    
      
    private void doSearch(String txt) {
        FindCustomerTask task=new FindCustomerTask();
        task.setFname(txt);
        task.setLname(txt);
        task.setOnSucceeded(e->{
            customers.clear();
            customers.addAll(task.getValue());
        });
        PonosExecutor.getInstance().getExecutor().submit(task);
    }

}
