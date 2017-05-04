/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers.customers;

import Messages.ConfirmationMessage;
import Messages.CustomerMessage;
import controllers.PonosControllerInterface;
import controllers.modals.ConfirmDialog;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import jpa.CustomerJpa;
import org.controlsfx.control.MaskerPane;
import org.controlsfx.control.Notifications;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import ponospos.entities.Customer;
import singletons.PonosExecutor;

/**
 * FXML Controller class
 *
 * @author Sawmtea
 */
public class CustomersController extends AnchorPane implements
        CustomerDialog.CustomerDialogListener,ConfirmDialog.ConfirmDialogListener,
        PonosControllerInterface{

    @FXML
    private TableView<Customer> customerTable;
    @FXML
    private TableColumn<Customer, Integer> idCol;
    @FXML
    private TableColumn<Customer, String> fnameCol;
    @FXML
    private TableColumn<Customer, String> lnameCol;
    @FXML
    private TableColumn<Customer, String> emailCol;
    @FXML
    private TableColumn<Customer, String> addressCol;
    @FXML
    private TableColumn<Customer,String> contactCol;
    @FXML
    private TableColumn<Customer, String> createdCol;
    @FXML
    private TableColumn<Customer, String> updateCol;
    @FXML
    private TableColumn<Void, Customer> actionCol;
    @FXML
    private Button newButton;
    @FXML
    private TextField searchField;
    @FXML
    private Button searchButton;
    @FXML
    private Label noOfCustomerlabel;
    private MaskerPane mask;
    private StackPane root;
    
    private ObservableList<Customer> customers=FXCollections.observableArrayList();
    /**
     * Initializes the controller class.
     */
      
    public CustomersController(MaskerPane mask,StackPane root){
        try {
            this.mask=mask;
            this.root=root;
            FXMLLoader loader=new FXMLLoader();
            loader.setLocation(this.getClass().getResource("/views/customers/customers.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
            newButton.setGraphic(new Glyph("FontAwesome", FontAwesome.Glyph.PLUS).color(Color.GREEN).size(48));
        } catch (IOException ex) {
            Logger.getLogger(CustomersController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
 
    @Override
    public void initControls() {
        customerTable.setItems(customers);
        //geta all customer and assign the return value to customers list
        idCol.setCellValueFactory(c->new SimpleObjectProperty<Integer>(c.getValue().getId()));
        fnameCol.setCellValueFactory(c->new SimpleStringProperty(c.getValue().getFirstName()));
        lnameCol.setCellValueFactory(c->new SimpleStringProperty(c.getValue().getLastName()));
        emailCol.setCellValueFactory(c->new SimpleStringProperty (c.getValue().getEmail()));
        addressCol.setCellValueFactory(c->new SimpleStringProperty(c.getValue().getAddress()));
        contactCol.setCellValueFactory(c->new SimpleStringProperty(c.getValue().getContact()));
        createdCol.setCellValueFactory(c->{
            Date date = c.getValue().getCreatedAt();
            SimpleDateFormat fm=new SimpleDateFormat("dd/MM/yy");          
            return new SimpleStringProperty(fm.format(date));
        });
        updateCol.setCellValueFactory(c->{
            Date date = c.getValue().getUpdatedAt();
            SimpleDateFormat fm=new SimpleDateFormat("dd/MM/yy");          
            return new SimpleStringProperty(fm.format(date));
        });
         actionCol.setCellFactory((TableColumn<Void, Customer> param) -> new TableCell<Void,Customer>(){
            Button editBtn=new Button("Edit",new Glyph("FontAwesome",FontAwesome.Glyph.EDIT).color(Color.BLUE));
            Button delBtn=new Button("Delete",new Glyph("FontAwesome",FontAwesome.Glyph.TRASH));
            
            @Override
            protected void updateItem(Customer item, boolean empty) {
                if (empty) {
                    setText(null);
                    setGraphic(null);
                }else{
                    editBtn.setOnAction(e->{
                        CustomerDialog d=new CustomerDialog(CustomersController.this);
                        d.setCustomer(customers.get(getIndex()));
                        d.isEditPurpose(true);
                        d.show(root);
                        
                    });
                    delBtn.setOnAction(e->{
                        ConfirmDialog dialog=new ConfirmDialog(ConfirmationMessage.TITLE,ConfirmationMessage.MESSAGE);
                        dialog.setListener(CustomersController.this);
                        dialog.setModel(customers.get(getIndex()));
                        dialog.show(root);
                    });
                    setGraphic(new HBox(editBtn,delBtn));
                }
            } 
        });
    }
    public void fetchAllCustomer(){
        FetchAllCustomerTask task=new FetchAllCustomerTask();
        task.setOnSucceeded(c->{
            List<Customer> all = task.getValue();
            customers.clear();
            customers.addAll(all);
        });
        mask.visibleProperty().bind(task.runningProperty());
        task.setOnFailed(e->task.getException().printStackTrace());       
        PonosExecutor.getInstance().getExecutor().submit(task);
    }
  
    @FXML
    private void doSearch(){
        FindCustomerTask task=new FindCustomerTask();
        task.setFname(searchField.getText().trim());
        task.setFname(searchField.getText().trim());
        mask.visibleProperty().bind(task.runningProperty());
        task.setOnFailed(c->{task.getException().printStackTrace();});
        task.setOnSucceeded(c->{
            customers.clear();
            customers.addAll(task.getValue());
        });
        PonosExecutor.getInstance().getExecutor().submit(task);
    }
    @FXML
    public void onNewButtonClick(ActionEvent e){
        CustomerDialog dialog=new CustomerDialog(CustomersController.this);
        dialog.show(root); 
    }

    @Override
    public void onCreate(Customer customer) {
        CreateTask task=new CreateTask();
        System.out.println(customer.getAddress());
        System.out.println(customer.getFirstName());
        task.setCustomer(customer);
        mask.visibleProperty().bind(task.runningProperty());
        task.setOnFailed(c->task.getException().printStackTrace(System.err));
        task.setOnSucceeded(c->{
            customers.add(task.getValue());
            Notifications.create()
                    .title(CustomerMessage.CREATE_SUCCESS_TITLE)
                    .text(CustomerMessage.DELETE_SUCCESS_MESSAGE)
                    .showInformation();
        });
        PonosExecutor.getInstance().getExecutor().submit(task);
    }

    @Override
    public void onEdit(Customer customer) {
        UpdateTask task=new UpdateTask();
        task.setCustomer(customer);
         mask.visibleProperty().bind(task.runningProperty());
        task.setOnFailed(c->task.getException().printStackTrace());
        task.setOnSucceeded(c->{
            customerTable.refresh();
            Notifications.create()
                    .title(CustomerMessage.UPDATE_SUCCESS_TITLE)
                    .text(CustomerMessage.UPDATE_SUCCESS_MESSAGE)
                    .showInformation();
        });
        PonosExecutor.getInstance().getExecutor().submit(task);
    }

    @Override
    public void onClickYes(Object obj) {
        DeleteTask task=new DeleteTask();
        task.setCustomer((Customer) obj);
        task.setOnFailed(c->task.getException().printStackTrace());
        mask.visibleProperty().bind(task.runningProperty());
        task.setOnSucceeded(c->{
            customers.remove(task.getValue());
            Notifications.create()
                    .title(CustomerMessage.DELETE_SUCCESS_TITLE)
                    .text(CustomerMessage.DELETE_SUCCESS_MESSAGE)
                    .showInformation();
        });
        PonosExecutor.getInstance().getExecutor().submit(task);
    }

    @Override
    public void initDependencies() {
        
    }

    @Override
    public void bindControls() {
         customers.addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                noOfCustomerlabel.setText(Integer.toString(customers.size()));
            }
        });
    }
    @Override
    public void hookupEvent() {
        customerTable.setRowFactory(c->{
            TableRow<Customer> row=new TableRow<>();
            row.setOnMouseClicked(e->{
            Customer customer = customers.get(row.getIndex());
                if (e.getClickCount()==2) {
                    displayCustomer(customer);
                }else if(e.getButton()==MouseButton.SECONDARY){
                    displayContextMenu(row,e.getScreenX(),e.getScreenY(),customer);
                }else{
                    //do nothing
                }
            });
            return row;
        });
    }

    private void displayCustomer(Customer customer) {
        CustomerDialog d=new CustomerDialog(this);
        d.setCustomer(customer);
        d.setIsViewPurpose(true);
        d.show(root);
    }

    private void displayContextMenu(Node node,double x, double y, Customer customer) {
        ContextMenu menu=new ContextMenu();
        
        Glyph icon=new Glyph("FontAwesome", FontAwesome.Glyph.TRASH).color(Color.CORAL).size(22);
        Glyph editIcon=new Glyph("FontAwesome", FontAwesome.Glyph.EDIT).color(Color.CORAL).size(22);
       
        MenuItem del=new MenuItem("Delete",icon);
        MenuItem edit=new MenuItem("Edit",editIcon);
        
        del.setOnAction(e->{
            ConfirmDialog d=new ConfirmDialog(ConfirmationMessage.TITLE,ConfirmationMessage.MESSAGE);
            d.setModel(customer);
            d.show(root);
        });
        edit.setOnAction(e->{
            CustomerDialog d=new CustomerDialog(CustomersController.this);
            d.setCustomer(customer);
            d.isEditPurpose(true);
            d.show(root);
            
        });
        menu.getItems().addAll(edit,del);
        menu.show(this.getScene().getWindow(), x, y);
        
//        PopOver pop=new PopOver();
//        
//        MenuItem editItem=new MenuItem("Edit");
//        MenuItem delItem=new MenuItem("delete");
//        pop.setContentNode(new Label("dfasd"));      
//        pop.show(node, x, y, Duration.ONE);
        
    }

    
    
    public class CreateTask extends Task<Customer>{
        private Customer customer;
        @Override
        protected Customer call() throws Exception {
            return CustomerJpa.createCustomer(customer);
        }

        public void setCustomer(Customer customer) {
            this.customer = customer;
        }
        
    }
     public class UpdateTask extends Task<Customer>{
         private Customer customer;
        @Override
        protected Customer call() throws Exception {
            return CustomerJpa.updateCustomer(customer);
        }
        public void setCustomer(Customer customer) {
            this.customer = customer;
        }
    
    }
    public class DeleteTask extends Task<Customer>{
          private Customer customer;
        @Override
        protected Customer call() throws Exception {
            return CustomerJpa.deleteCustomer(customer);
        }
        public void setCustomer(Customer customer) {
            this.customer = customer;
        }
    
    }
    public class FetchAllCustomerTask extends Task<List<Customer>>{
        @Override
        protected List<Customer> call() throws Exception {
            return CustomerJpa.getAllCustomers();
        } 
    }
    public class FindCustomerTask extends Task<List<Customer>>{
        private String fname;
        private String lname;
        @Override
        protected List<Customer> call() throws Exception {
            Thread.sleep(5000);
            return CustomerJpa.findCustomerByName(fname, lname);
        } 

        public void setFname(String fname) {
            this.fname = fname;
        }

        public void setLname(String lname) {
            this.lname = lname;
        }
        
    }
      
}
