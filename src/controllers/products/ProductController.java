/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers.products;

import Messages.ConfirmationMessage;
import Messages.ProductMessage;
import com.jfoenix.controls.JFXButton;
import controllers.PonosControllerInterface;
import controllers.categories.CategoryController;
import controllers.modals.ConfirmDialog;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
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
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import org.controlsfx.control.MaskerPane;
import org.controlsfx.control.Notifications;
import ponospos.entities.Category;
import ponospos.entities.Product;
import singletons.PonosExecutor;
import tasks.products.CreateTask;
import tasks.products.DeleteTask;
import tasks.products.EditTask;
import tasks.products.FetchAllTask;

/**
 * FXML Controller class
 *
 * @author Sawmtea
 */
public class ProductController extends AnchorPane 
implements PonosControllerInterface,
        ProductDialog.ProductDialogListener,
        ConfirmDialog.ConfirmDialogListener{

    private StackPane root;
    private MaskerPane mask;
    @FXML
    private AnchorPane AnchorPane;
    @FXML
    private JFXButton newProductbtn;
    
    
   
    @FXML
    private TableView<Product> productTable;
    @FXML
    private TableColumn<Product, Integer> idCol;
    @FXML
    private TableColumn<Product, String> nameCol;
    @FXML
    private TableColumn<Product, String> prizeCol;
    @FXML
    private TableColumn<Product, Boolean> activeCol;
    @FXML
    private TableColumn<Product, String> addedbyCol;
    @FXML
    private TableColumn<Product, Product> actionCols;
    
    private ObservableList<Product> products=FXCollections.observableArrayList();
    private ObservableList<Category> categories=FXCollections.observableArrayList();
    
    public ProductController(MaskerPane mask,StackPane root){
        try {
            this.root=root;
            this.mask=mask;
            
            FXMLLoader loader=new FXMLLoader();
            loader.setLocation(this.getClass().getResource("/views/products/products.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
            this.productTable.setItems(products);
        } catch (IOException ex) {
            Logger.getLogger(ProductController.class.getName()).log(Level.SEVERE, null, ex);
        }
    } 

    @Override
    public void initDependencies() {
    }

    @Override
    public void initControls() {
        idCol.setCellValueFactory(e->new SimpleObjectProperty<>(e.getValue().getId()));
        nameCol.setCellValueFactory(e->new SimpleStringProperty(e.getValue().getName()));
        prizeCol.setCellValueFactory(e->{
            Product value = e.getValue();
            BigDecimal sellingPrice = value.getSellingPrice();
            String str = NumberFormat.getCurrencyInstance(new Locale("en","in")).format(sellingPrice.doubleValue());
            return new SimpleStringProperty(str);
        });
        activeCol.setCellValueFactory(e->new SimpleObjectProperty<Boolean>(e.getValue().getIsActive()));
        addedbyCol.setCellValueFactory(e->new SimpleStringProperty(e.getValue().getAddedBy().getUsername()));
        actionCols.setCellFactory((TableColumn<Product, Product> param) -> 
            new TableCell<Product,Product>(){
                FontAwesomeIconView viewIcon=new FontAwesomeIconView(FontAwesomeIcon.EYE);
                FontAwesomeIconView editIcon=new FontAwesomeIconView(FontAwesomeIcon.EDIT);
                FontAwesomeIconView delIcon=new FontAwesomeIconView(FontAwesomeIcon.TRASH);
                Button viewBtn=new Button("",viewIcon);
                Button editBtn=new Button("",editIcon);
                Button delBtn=new Button("",delIcon);
                
                @Override
                protected void updateItem(Product item, boolean empty) {
                    super.updateItem(item, empty); 
                    if (empty) {
                        setText(null);
                        setGraphic(null);
                    }else{
                        viewBtn.setOnAction(e->{
                            
                        });
                        editBtn.setOnAction(e->{
                        });
                        delBtn.setOnAction(e->{
                             ConfirmDialog d=new ConfirmDialog(ConfirmationMessage.TITLE, ConfirmationMessage.MESSAGE);
                            Product model = products.get(getIndex());
                            d.setModel(model);
                            d.setListener(ProductController.this);
                            d.show(root);
                        });
                        setGraphic(new HBox(5,viewBtn,editBtn,delBtn));
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
    
    public void fetchAllProducts(){
        FetchAllTask task=new FetchAllTask();
        task.setOnSucceeded(e->{
            products.clear();
            products.addAll(task.getValue());
        });
        task.setOnFailed(e->task.getException().printStackTrace(System.err));
        mask.visibleProperty().bind(task.runningProperty());
        
        tasks.categories.FetchAllTask t2=new tasks.categories.FetchAllTask();
        t2.setOnSucceeded(e->{
            categories.clear();
            categories.addAll(t2.getValue());
            System.out.println(t2.getValue());
        });
        t2.setOnFailed(e->task.getException().printStackTrace(System.err));
        mask.visibleProperty().bind(t2.runningProperty());
        PonosExecutor.getInstance().getExecutor().submit(task);
        PonosExecutor.getInstance().getExecutor().submit(t2);

    }

    @FXML
    private void onNewProductBtnClick(ActionEvent event) {
        ProductDialog d=new ProductDialog(this);
        d.isCreate().setCategories(categories);
        d.show(root);
        
    }

    @Override
    public void onCreate(Product product) {
        CreateTask task=new CreateTask();
        task.setOnSucceeded(e->{
            products.add(task.getValue());
            Notifications.create()
                .title(ProductMessage.CREATE_SUCCESS_TITLE)
                .text(ProductMessage.CREATE_SUCCESS_MESSAGE)
                .showInformation();});
        task.setOnFailed(e->task.getException().printStackTrace(System.err));
        mask.visibleProperty().bind(task.runningProperty());
        task.setProduct(product);
        PonosExecutor.getInstance().getExecutor().submit(task);
    }

    @Override
    public void onUpdate(Product product) {
        EditTask task=new EditTask();
        task.setOnSucceeded(e->Notifications.create()
                .title(ProductMessage.UPDATE_SUCCESS_TITLE)
                .text(ProductMessage.UPDATE_SUCCESS_MESSAGE)
                .showInformation());
        task.setOnFailed(e->task.getException().printStackTrace(System.err));
        mask.visibleProperty().bind(task.runningProperty());
        task.setProduct(product);
        PonosExecutor.getInstance().getExecutor().submit(task);
    }

    
    @Override
    public void onClickYes(Object obj) {
        DeleteTask task=new DeleteTask();
        task.setOnSucceeded(e->{
            products.remove(task.getValue());
            Notifications.create()
                .title(ProductMessage.DELETE_SUCCESS_TITLE)
                .text(ProductMessage.DELETE_SUCCESS_MESSAGE)
                .showInformation();}
        );
        task.setOnFailed(e->task.getException().printStackTrace(System.err));
        mask.visibleProperty().bind(task.runningProperty());
        task.setProduct((Product) obj);
        PonosExecutor.getInstance().getExecutor().submit(task);
    }
    
}
