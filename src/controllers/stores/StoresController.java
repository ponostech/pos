/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers.stores;

import Messages.ConfirmationMessage;
import Messages.StoreMessage;
import com.jfoenix.controls.JFXButton;
import controllers.PonosControllerInterface;
import controllers.modals.ConfirmDialog;
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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Hyperlink;
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
import javafx.stage.Window;
import org.controlsfx.control.MaskerPane;
import org.controlsfx.control.Notifications;
import ponospos.entities.Product;
import ponospos.entities.Stores;
import singletons.PonosExecutor;
import tasks.stores.CreateTask;
import tasks.stores.DeleteTask;
import tasks.stores.FetchAllTask;
import tasks.stores.FindStoreByNameTask;
import tasks.stores.FindStoreStock;
import tasks.stores.UpdateTask;

/**
 * FXML Controller class
 *
 * @author Sawmtea
 */
public class StoresController extends AnchorPane implements 
        PonosControllerInterface,ConfirmDialog.ConfirmDialogListener,StoreDialog.StoreDialogListener{

    @FXML
    private JFXButton newStoreBtn;
    @FXML
    private TextField searchField;
    @FXML
    private Button searchBtn;
    @FXML
    private TableView<Stores> storeTables;
    @FXML
    private TableColumn<Stores, Integer> idCol;
    @FXML
    private TableColumn<Stores, String> nameCol;
    @FXML
    private TableColumn<Stores, String> addressCol;
    @FXML
    private TableColumn<Stores, Stores> actionCols;
    
    private MaskerPane mask;
    private StackPane root;
    private ObservableList<Stores> stores=FXCollections.observableArrayList();

    /**
     * Initializes the controller class.
     */
       
    public StoresController(MaskerPane mask,StackPane root){
        this.root=root;
        this.mask=mask;
        try {
            FXMLLoader loader=new FXMLLoader();
            loader.setLocation(this.getClass().getResource("/views/stores/stores.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (IOException ex) {
            Logger.getLogger(StoresController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    @FXML
    public void onNewStoreBtnClick(ActionEvent event){
        new StoreDialog(this).createStore().show(root);
        
    }
    @Override
    public void onClickYes(Object obj) {
        DeleteTask task=new DeleteTask();
        task.setStore((Stores) obj);
        mask.visibleProperty().bind(task.runningProperty());
        task.setOnFailed(e->task.getException().printStackTrace(System.err));
        task.setOnSucceeded(e->{
            Notifications.create().title(StoreMessage.STORE_DELETION_SUCCESS_TITLE)
                    .text(StoreMessage.STORE_DELETION_SUCCESS_TEXT).showInformation();
            stores.remove(task.getValue());
                });
        PonosExecutor.getInstance().getExecutor().submit(task);
    }

    /**
     *save the store info to database and then add an instance into list
     * @param store
     */
    @Override
    public void onCreate(Stores store) {
        CreateTask task=new CreateTask();
        task.setStore(store);
        mask.visibleProperty().bind(task.runningProperty());
        task.setOnFailed(e->task.getException().printStackTrace(System.err));
        task.setOnSucceeded(e->{
            Notifications.create().title(StoreMessage.STORE_INSERTION_SUCCESS_TITLE).text(StoreMessage.STORE_INSERTION_SUCCESS_TEXT).showInformation();
            stores.add(task.getValue());
                });
        PonosExecutor.getInstance().getExecutor().submit(task);
    }

    @Override
    public void onUpdate(Stores store) {
        UpdateTask task=new UpdateTask();
        task.setStore(store);
        mask.visibleProperty().bind(task.runningProperty());
        task.setOnFailed(e->task.getException().printStackTrace(System.err));
        task.setOnSucceeded(e->{
            storeTables.refresh();
            Notifications.create().title(StoreMessage.STORE_UPDATE_SUCCESS_TITLE).text(StoreMessage.STORE_UPDATE_SUCCESS_TEXT).showInformation();
                });
        PonosExecutor.getInstance().getExecutor().submit(task);
    }

    
    @Override
    public void initDependencies() {
    }

    @Override
    public void initControls() {
        storeTables.setItems(stores);
        idCol.setCellValueFactory(e->new SimpleObjectProperty(e.getValue().getId()));
        nameCol.setCellValueFactory(e->new SimpleStringProperty(e.getValue().getName()));
        addressCol.setCellValueFactory(e->new SimpleStringProperty(e.getValue().getAddress()));
        actionCols.setCellFactory((TableColumn<Stores, Stores> param) -> 
            new TableCell<Stores,Stores>(){
                FontAwesomeIconView viewIcon=new FontAwesomeIconView(FontAwesomeIcon.EYE);
                FontAwesomeIconView editIcon=new FontAwesomeIconView(FontAwesomeIcon.EDIT);
                FontAwesomeIconView delIcon=new FontAwesomeIconView(FontAwesomeIcon.TRASH);
                FontAwesomeIconView stockIcon=new FontAwesomeIconView(FontAwesomeIcon.LIST);
                Button viewBtn=new Button("",viewIcon);
                Button editBtn=new Button("",editIcon);
                Button delBtn=new Button("",delIcon);
                Hyperlink stockLink=new Hyperlink("All stocks", stockIcon);
                
                @Override
                protected void updateItem(Stores item, boolean empty) {
                    super.updateItem(item, empty); 
                    if (empty) {
                        setText(null);
                        setGraphic(null);
                    }else{
                        viewBtn.setOnAction(e->{
                            new StoreDialog(StoresController.this).model(stores.get(getIndex())).viewStore().show(root);
                        });
                        editBtn.setOnAction(e->{
                            new StoreDialog(StoresController.this).model(stores.get(getIndex())).updateStore().show(root);
                        });
                        delBtn.setOnAction(e->{
                            Stores model = stores.get(getIndex());
                            ConfirmDialog d=new ConfirmDialog(ConfirmationMessage.TITLE, ConfirmationMessage.MESSAGE);
                            d.setModel(model);
                            d.setListener(StoresController.this);
                            d.show(root);
                        });
                        stockLink.setOnAction(e->{
                            Stores store = stores.get(getIndex());
                            FindStoreStock task=new FindStoreStock();
                            task.store=store;
                            mask.visibleProperty().bind(task.runningProperty());
                            task.setOnSucceeded(ev->{
                                Set<Product> set = new HashSet<>();
                                set.addAll(task.getValue());
                                StoreStockController d=new StoreStockController();
                                d.setStore(store);
                                d.setProducts(set);
                                d.show(root);
                            });
                            task.setOnFailed(ev->{
                                task.getException().printStackTrace(System.err);
                            });
                            PonosExecutor.getInstance().getExecutor().submit(task);
                        });
                        setGraphic(new HBox(10,stockLink,viewBtn,editBtn,delBtn));
                    }
                }
                
            }
        );
    }

    @Override
    public void bindControls() {
        searchField.textProperty().addListener(e->doSearch());
    }

    /**
     *
     */
    @Override
    public void hookupEvent() {
        searchBtn.setOnAction(e->doSearch());
        storeTables.setRowFactory(c->{
            TableRow<Stores> row = new TableRow<Stores>();
            row.setOnMouseClicked(e->{
                 List<Stores> selectedItems=storeTables.getSelectionModel().getSelectedItems();
                if (e.getClickCount()==2) {
                    new StoreDialog(StoresController.this).model(stores.get(row.getIndex())).viewStore().show(root);
                }
                else if (e.getButton()==MouseButton.SECONDARY) {
                    displayPopOver(e.getScreenX(),e.getScreenY(),selectedItems);

                }else{
                    //TODO::nothing
                }
            });
            return row;
            
        });
    }

    @Override
    public void controlFocus() {
    }

    public void fetchAllStores(){
       FetchAllTask task=new FetchAllTask();
        mask.visibleProperty().bind(task.runningProperty());
        task.setOnFailed(e->task.getException().printStackTrace(System.err));
        task.setOnSucceeded(e->{
            stores.clear();
            stores.addAll(task.getValue());
        });
        PonosExecutor.getInstance().getExecutor().submit(task);
    }

    private void doSearch() {
        FindStoreByNameTask task=new FindStoreByNameTask();
        task.setParam(searchField.getText().trim());
        task.setOnSucceeded(e->{
            stores.clear();
            stores.addAll(task.getValue());
        });
        task.setOnFailed(e->task.getException().printStackTrace(System.err));
        PonosExecutor.getInstance().getExecutor().submit(task);
    }

    private void displayPopOver(double x, double y, List<Stores> selectedItems) {
        FontAwesomeIconView editIcon=new FontAwesomeIconView(FontAwesomeIcon.EDIT);
        FontAwesomeIconView delIcon=new FontAwesomeIconView(FontAwesomeIcon.TRASH);
        FontAwesomeIconView viewIcon=new FontAwesomeIconView(FontAwesomeIcon.EYE);
        
        viewIcon.setFill(Color.CORAL);
        editIcon.setFill(Color.CORAL);
        delIcon.setFill(Color.CORAL);
                
        MenuItem view=new MenuItem("View",viewIcon);
        MenuItem edit=new MenuItem("Edit",editIcon);
        MenuItem del=new MenuItem("Delete",delIcon);
        
        ContextMenu menu=new ContextMenu();
        
        if (selectedItems.size()>1) {
            menu.getItems().add(del);
        }else{
            menu.getItems().addAll(view,edit,del);
        }
        
        view.setOnAction(e->{
            new StoreDialog(StoresController.this).model(selectedItems.get(0)).viewStore().show(root);
        });
        del.setOnAction(e->{
                for (Stores selectedItem : selectedItems) {
                
                    ConfirmDialog d=new ConfirmDialog(ConfirmationMessage.TITLE,ConfirmationMessage.MESSAGE+" "
                        + "" +selectedItem.getName());
                    d.setModel(selectedItem);
                    d.setListener(StoresController.this);
                    d.show(root);
                }
            });
        edit.setOnAction(e->{
             new StoreDialog(StoresController.this).model(selectedItems.get(0)).updateStore().show(root);
        });
        Window window = this.getScene().getWindow();
        menu.show(window,x,y);
    }
    
    
}
