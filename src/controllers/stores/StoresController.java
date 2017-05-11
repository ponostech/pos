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
import ponospos.entities.Stores;
import singletons.PonosExecutor;
import tasks.stores.CreateTask;
import tasks.stores.DeleteTask;
import tasks.stores.FetchAllTask;
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
        nameCol.setCellValueFactory(e->new SimpleStringProperty(e.getValue().getAddress()));
        addressCol.setCellValueFactory(e->new SimpleStringProperty(e.getValue().getAddress()));
        actionCols.setCellFactory((TableColumn<Stores, Stores> param) -> 
            new TableCell<Stores,Stores>(){
                FontAwesomeIconView viewIcon=new FontAwesomeIconView(FontAwesomeIcon.EYE);
                FontAwesomeIconView editIcon=new FontAwesomeIconView(FontAwesomeIcon.EDIT);
                FontAwesomeIconView delIcon=new FontAwesomeIconView(FontAwesomeIcon.TRASH);
                Button viewBtn=new Button("",viewIcon);
                Button editBtn=new Button("",editIcon);
                Button delBtn=new Button("",delIcon);
                @Override
                protected void updateItem(Stores item, boolean empty) {
                    super.updateItem(item, empty); 
                    if (empty) {
                        setText(null);
                        setGraphic(null);
                    }else{
                        viewBtn.setOnAction(e->{
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
    
    
}
