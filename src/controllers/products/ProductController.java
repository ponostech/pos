/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers.products;

import Messages.CategoryMessage;
import Messages.ConfirmationMessage;
import Messages.ProductMessage;
import Messages.SupplierMessage;
import com.jfoenix.controls.JFXButton;
import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;
import controllers.PonosControllerInterface;
import controllers.categories.CategoryDialog;
import controllers.modals.ConfirmDialog;
import controllers.modals.ExceptionDialog;
import controllers.suppliers.SupplierDialog;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.List;
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
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import org.controlsfx.control.MaskerPane;
import org.controlsfx.control.Notifications;
import org.eclipse.persistence.exceptions.DatabaseException;
import ponospos.entities.Category;
import ponospos.entities.Product;
import ponospos.entities.Stores;
import ponospos.entities.Supplier;
import singletons.PonosExecutor;
import tasks.products.CreateTask;
import tasks.products.DeleteTask;
import tasks.products.EditTask;
import tasks.products.FetchAllTask;
import tasks.products.FindProductTask;

/**
 * FXML Controller class
 *
 * @author Sawmtea
 */
public class ProductController extends AnchorPane 
implements PonosControllerInterface,
        ProductDialog.ProductDialogListener,
        ConfirmDialog.ConfirmDialogListener,
        SupplierDialog.SupplierDialogListener,
        CategoryDialog.CategoryDialogListener{

    private StackPane root;
    private MaskerPane mask;
    @FXML
    private AnchorPane AnchorPane;
    @FXML
    private JFXButton newProductbtn;
    @FXML
    private TextField searchField;
    @FXML
    private Button searchBtn;
    
   
    @FXML
    private TableView<Product> productTable1;
    @FXML
    private TableColumn<Product, Integer> idCol1;
    @FXML
    private TableColumn<Product, String> nameCol1;
    @FXML
    private TableColumn<Product, String> prizeCol1;
    @FXML
    private TableColumn<Product, Boolean> activeCol1;
    @FXML
    private TableColumn<Product, String> addedbyCol1;
    @FXML
    private TableColumn<Product, Product> actionCols1;
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
    
    private ObservableList<Product> inactiveProducts=FXCollections.observableArrayList();
    private ObservableList<Product> products=FXCollections.observableArrayList();
    private ObservableList<Category> categories=FXCollections.observableArrayList();
    private ObservableList<Supplier>suppliers=FXCollections.observableArrayList();
    private ObservableList<Stores>stores=FXCollections.observableArrayList();
    
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
            this.productTable1.setItems(inactiveProducts);
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
        activeCol.setCellValueFactory(e->new SimpleObjectProperty<Boolean>(e.getValue().isActive()));
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
                           new ProductDetailController().populate(products.get(getIndex())).show(root);
                            
                        });
                        editBtn.setOnAction(e->{
                            ProductDialog d=new ProductDialog(ProductController.this);
                            d.setCategories(categories);
                            d.setSuppliers(suppliers);
                            d.setProduct(products.get(getIndex()));
                            d.isEdit().show(root);
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
        idCol1.setCellValueFactory(e->new SimpleObjectProperty<>(e.getValue().getId()));
        nameCol1.setCellValueFactory(e->new SimpleStringProperty(e.getValue().getName()));
        prizeCol1.setCellValueFactory(e->{
            Product value = e.getValue();
            BigDecimal sellingPrice = value.getSellingPrice();
            String str = NumberFormat.getCurrencyInstance(new Locale("en","in")).format(sellingPrice.doubleValue());
            return new SimpleStringProperty(str);
        });
        activeCol1.setCellValueFactory(e->new SimpleObjectProperty<Boolean>(e.getValue().isActive()));
        addedbyCol1.setCellValueFactory(e->new SimpleStringProperty(e.getValue().getAddedBy().getUsername()));
        actionCols1.setCellFactory((TableColumn<Product, Product> param) -> 
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
                            new ProductDetailController().populate(inactiveProducts.get(getIndex())).show(root);
                        });
                        editBtn.setOnAction(e->{
                            ProductDialog d=new ProductDialog(ProductController.this);
                            d.setCategories(categories);
                            d.setSuppliers(suppliers);
                            d.setProduct(inactiveProducts.get(getIndex()));
                            d.isEdit().show(root);
                        });
                        delBtn.setOnAction(e->{
                             ConfirmDialog d=new ConfirmDialog(ConfirmationMessage.TITLE, ConfirmationMessage.MESSAGE);
                            Product model = inactiveProducts.get(getIndex());
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
        searchField.textProperty().addListener(e->doSearch());
        searchBtn.setOnAction(e->doSearch());
        
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
            inactiveProducts.clear();
            for (Product p : task.getValue()) {
                if (p.isActive()) {
                    products.add(p);
                } else {
                    inactiveProducts.add(p);
                }
            }
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
        
        tasks.suppliers.FetchAllTask t4=new tasks.suppliers.FetchAllTask();
        t4.setOnSucceeded(e->{
            suppliers.clear();
            suppliers.addAll(t4.getValue());
        });
        t4.setOnFailed(e -> task.getException().printStackTrace(System.err));

        tasks.stores.FetchAllTask t5=new tasks.stores.FetchAllTask();
        mask.visibleProperty().bind(t5.runningProperty());
        t5.setOnSucceeded(e->{
            stores.clear();
            stores.addAll(t5.getValue());
        });
        t5.setOnFailed(ev->t5.getException().printStackTrace(System.err));
        PonosExecutor.getInstance().getExecutor().submit(task);
        PonosExecutor.getInstance().getExecutor().submit(t2);
        PonosExecutor.getInstance().getExecutor().submit(t4);
        PonosExecutor.getInstance().getExecutor().submit(t5);

    }

    @FXML
    private void onNewProductBtnClick(ActionEvent event) {
       ProductDialog d=new ProductDialog(this);
       d.setCategories(categories);
       d.setSuppliers(suppliers);
       d.setStores(stores);
       d.isCreate();
       d.show(root);
        
    }
    @FXML
    private void onNewCategoryClick(ActionEvent event) {
       new CategoryDialog(this).isCreate(true).show(root);        
    }
    @FXML
    private void onNewSuppliersClick(ActionEvent event) {
        SupplierDialog d = new SupplierDialog(this);
        d.isCreate().show(root);
        
    }

    @Override
    public void onCreate(Product product) {
        CreateTask task=new CreateTask();
        task.setOnSucceeded(e->{
            if (task.getValue().isActive()) {
                products.add(task.getValue());
            }else{
                inactiveProducts.add(task.getValue());
            }
            Notifications.create()
                .title(ProductMessage.CREATE_SUCCESS_TITLE)
                .text(ProductMessage.CREATE_SUCCESS_MESSAGE)
                .showInformation();});
        task.setOnFailed(e->{
            ExceptionDialog d=new ExceptionDialog(task.getException());
            for (Throwable t = task.getException().getCause(); t != null; t = t.getCause()) {
                System.err.println(((MySQLIntegrityConstraintViolationException) t).getErrorCode());
                if (t instanceof MySQLIntegrityConstraintViolationException ) {
                    Notifications.create().title(null).text(ProductMessage.DUPLICATE_BARCODE_MESSAGE).showError();
                    return;
                }
            }
//            task.getException().printStackTrace(System.err);
            if (task.getException() instanceof MySQLIntegrityConstraintViolationException) {
                System.out.println("tuype od exception "+task.getException().getMessage());
            }
            });
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

    @Override
    public void onUpdate(Product product) {
        EditTask task=new EditTask();
        task.setProduct(product);
        task.setOnFailed(e->task.getException().printStackTrace(System.err));
        task.setOnSucceeded(e->{
            Notifications.create().title(ProductMessage.UPDATE_SUCCESS_TITLE)
                    .text(ProductMessage.UPDATE_SUCCESS_MESSAGE)
                    .showInformation();
            if (!task.getValue().isActive()) {
                if (products.contains(task.getValue())) {
                    products.remove(task.getValue());
                    inactiveProducts.add(task.getValue());
                }else {
                    inactiveProducts.remove(task.getValue());
                    products.add(task.getValue());
                }
            }else {
                if (products.contains(task.getValue())) {
                    products.remove(task.getValue());
                    inactiveProducts.add(task.getValue());
                } else if (inactiveProducts.contains(task.getValue())) {
                    inactiveProducts.remove(task.getValue());
                    products.add(task.getValue());
                }
            }
            productTable.refresh();
            productTable1.refresh();
        });
        mask.visibleProperty().bind(task.runningProperty());
        PonosExecutor.getInstance().getExecutor().submit(task);

    }

    @Override
    public void onCreateSupplier(Supplier supplier) {
        tasks.suppliers.CreateTask task = new tasks.suppliers.CreateTask();
        task.setSupplier(supplier);
        mask.visibleProperty().bind(task.runningProperty());
        task.setOnSucceeded(e -> {
            suppliers.add(task.getValue());
            Notifications.create().title(SupplierMessage.CREATE_SUCCESS_TITLE).text(SupplierMessage.CREATE_SUCCESS_TEXT).showInformation();
        });
        task.setOnFailed(e -> {
            task.getException().printStackTrace(System.err);
        });
        PonosExecutor.getInstance().getExecutor().submit(task);
    }

    @Override
    public void onEditSupplier(Supplier supplier) {
    }

    @Override
    public void onCreate(Category category) {
        tasks.categories.CreateTask task = new tasks.categories.CreateTask();
        task.setCategory(category);
        mask.visibleProperty().bind(task.runningProperty());
        task.setOnSucceeded(e -> {
            categories.add(task.getValue());
            Notifications.create().title(CategoryMessage.CREATE_SUCCESS_TITLE).text(CategoryMessage.CREATE_SUCCESS_MESSAGE).showInformation();
        });
        task.setOnFailed(e -> task.getException().printStackTrace(System.err));
        PonosExecutor.getInstance().getExecutor().submit(task);
    }

    @Override
    public void onUpdate(Category category) {
    }

   
    private void doSearch(){
        String text=searchField.getText().trim();
        tasks.products.FindProductTask task=new FindProductTask();
        task.setParam(text);
        mask.visibleProperty().bind(task.runningProperty());
        task.setOnSucceeded(e->{
            List<Product> ps = task.getValue();
            products.clear();
            inactiveProducts.clear();
            for (Product p : ps) {
                if (p.isActive()) {
                    products.add(p);
                }else{
                    inactiveProducts.add(p);
                }
            }
            productTable.refresh();
            productTable1.refresh();
        });
        PonosExecutor.getInstance().getExecutor().submit(task);
    }
}
