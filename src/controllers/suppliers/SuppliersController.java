/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers.suppliers;

import Messages.ConfirmationMessage;
import Messages.SupplierMessage;
import com.jfoenix.controls.JFXButton;
import controllers.PonosControllerInterface;
import controllers.modals.ConfirmDialog;
import controllers.users.UserDialog;
import controllers.users.UsersController;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.io.IOException;
import java.util.List;
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
import ponospos.entities.Supplier;
import ponospos.entities.User;
import singletons.PonosExecutor;
import tasks.suppliers.CreateTask;
import tasks.suppliers.DeleteTask;
import tasks.suppliers.FetchAllTask;
import tasks.suppliers.SearchSupplierByNameTask;
import tasks.suppliers.UpdateTask;
import util.Role;

/**
 *
 * @author Sawmtea
 */
public class SuppliersController extends AnchorPane implements 
        SupplierDialog.SupplierDialogListener,
        ConfirmDialog.ConfirmDialogListener,PonosControllerInterface{

    @FXML
    private TableView<Supplier>supplierTable; 
    @FXML
    private AnchorPane AnchorPane;
    @FXML
    private TableColumn<Supplier, Integer> idCol;
    @FXML
    private TableColumn<Supplier, String> nameCol;
    @FXML
    private TableColumn<Supplier, String> contactCol;
    @FXML
    private TableColumn<Supplier, Supplier> actionCol;
    @FXML
    private JFXButton newSupplierBtn;
    @FXML
    private TextField searchField;
    @FXML
    private Button searchBtn;
    private ObservableList<Supplier> suppliers=FXCollections.observableArrayList();
    
    private StackPane root;
    private MaskerPane mask;

    public SuppliersController(MaskerPane mask,StackPane root) {
        this.root=root;
        this.mask=mask;
        try {
            FXMLLoader loader=new FXMLLoader();
            loader.setLocation(this.getClass().getResource("/views/suppliers/suppliers.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (IOException ex) {
            Logger.getLogger(SuppliersController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
   
    @FXML
    private void onNewSupplierBtnClick(ActionEvent event) {
        SupplierDialog d=new SupplierDialog(this);
        d.isCreate().show(root);
    }    
    @Override
    public void initDependencies() {
    }

    @Override
    public void initControls() {
        supplierTable.setItems(suppliers);
        idCol.setCellValueFactory(e->new SimpleObjectProperty(e.getValue().getId()));
        nameCol.setCellValueFactory(e->new SimpleStringProperty(e.getValue().getName()));
        contactCol.setCellValueFactory(e->new SimpleStringProperty(e.getValue().getContact()));
        actionCol.setCellFactory((TableColumn<Supplier, Supplier> param) -> 
            new TableCell<Supplier,Supplier>(){
                FontAwesomeIconView viewIcon=new FontAwesomeIconView(FontAwesomeIcon.EYE);
                FontAwesomeIconView editIcon=new FontAwesomeIconView(FontAwesomeIcon.EDIT);
                FontAwesomeIconView delIcon=new FontAwesomeIconView(FontAwesomeIcon.TRASH);
                Button viewBtn=new Button("",viewIcon);
                Button editBtn=new Button("",editIcon);
                Button delBtn=new Button("",delIcon);
                @Override
                protected void updateItem(Supplier item, boolean empty) {
                    super.updateItem(item, empty); 
                    if (empty) {
                        setText(null);
                        setGraphic(null);
                    }else{
                        viewBtn.setOnAction(e->{
                            new SupplierDialog(SuppliersController.this).setModels(suppliers.get(getIndex())).isView().show(root);
                        });
                        editBtn.setOnAction(e->{
                            new SupplierDialog(SuppliersController.this).isEdit().toUpdateModel(suppliers.get(getIndex())).show(root);
                            
                        });
                        delBtn.setOnAction(e->{
                            Supplier model = suppliers.get(getIndex());
                            ConfirmDialog d=new ConfirmDialog(ConfirmationMessage.TITLE, ConfirmationMessage.MESSAGE);
                            d.setModel(model);
                            d.setListener(SuppliersController.this);
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
    }

    @Override
    public void hookupEvent() {
        searchBtn.setOnAction(e->doSearch());
        supplierTable.setRowFactory(p->{
            TableRow<Supplier> row=new TableRow();
            
            row.setOnMouseClicked(e->{
                List<Supplier> selectedItems=supplierTable.getSelectionModel().getSelectedItems();
                
                if ( e.getButton()==MouseButton.SECONDARY) {
                    displayPopOver(e.getScreenX(),e.getScreenY(),selectedItems);
                }else if(e.getClickCount()==2){
                   new SupplierDialog(SuppliersController.this).setModels(suppliers.get(row.getIndex())).isView().show(root);
                }else{
                    //do nothing
                }
            });
            return row;
        });
    }

    @Override
    public void controlFocus() {
    }

    @Override
    public void onCreateSupplier(Supplier supplier) {
        CreateTask task=new CreateTask();
        task.setSupplier(supplier);
        mask.visibleProperty().bind(task.runningProperty());
        task.setOnSucceeded(e->{
            suppliers.add(task.getValue());
            Notifications.create().title(SupplierMessage.CREATE_SUCCESS_TITLE).text(SupplierMessage.CREATE_SUCCESS_TEXT).showInformation();
        });
        task.setOnFailed(e->{
            task.getException().printStackTrace(System.err);
        });
        PonosExecutor.getInstance().getExecutor().submit(task);
    }
    
    @Override
    public void onEditSupplier(Supplier supplier) {
        UpdateTask task=new UpdateTask();
        task.setSupplier(supplier);
        mask.visibleProperty().bind(task.runningProperty());
        task.setOnSucceeded(e->{     
            supplierTable.refresh();
            Notifications.create().title(SupplierMessage.UPDATE_SUCCESS_TITLE).text(SupplierMessage.UPDATE_SUCCESS_TEXT).showInformation();
        });
        task.setOnFailed(e->{
            task.getException().printStackTrace(System.err);
        });
        PonosExecutor.getInstance().getExecutor().submit(task);
    }
    
    public void fetchAllSupplier(){
        FetchAllTask task=new FetchAllTask();
        mask.visibleProperty().bind(task.runningProperty());
        task.setOnFailed(e->task.getException().printStackTrace(System.err));
        task.setOnSucceeded(e->{
            suppliers.clear();
            suppliers.addAll(task.getValue());
        });
        PonosExecutor.getInstance().getExecutor().submit(task); 
    }

    @Override
    public void onClickYes(Object obj) {
        DeleteTask task=new DeleteTask();
        task.setOnSucceeded(e->{
            suppliers.remove(task.getValue());
            Notifications.create().title(SupplierMessage.DELETE_SUCCESS_TITLE).text(SupplierMessage.DELETE_SUCCESS_TEXT).showInformation();

        });
        task.setSupplier((Supplier) obj);
        mask.visibleProperty().bind(task.runningProperty());
        task.setOnFailed(e->task.getException().printStackTrace(System.err));
        PonosExecutor.getInstance().getExecutor().submit(task);
    }

    private void doSearch() {
        SearchSupplierByNameTask task=new SearchSupplierByNameTask();
        task.setName(searchField.getText().trim());
        task.setOnSucceeded(e->{
            suppliers.clear();
            suppliers.addAll(task.getValue());
        });
        task.setOnFailed(e->task.getException().printStackTrace(System.err));
        PonosExecutor.getInstance().getExecutor().submit(task);
    }
     private void displayPopOver(double x,double y,List<Supplier> selectedItems){
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
        
        view.setOnAction(e-> new SupplierDialog(SuppliersController.this)
                .setModels(selectedItems.get(0))
                .isView()
                .show(root));

        del.setOnAction(e->{
            for (Supplier selectedItem : selectedItems) {
                    ConfirmDialog d=new ConfirmDialog(ConfirmationMessage.TITLE,ConfirmationMessage.MESSAGE+" "
                        + "" +selectedItem.getName());
                    d.setModel(selectedItem);
                    d.setListener(SuppliersController.this);
                    d.show(root);
            }
        });
        edit.setOnAction(e->{
           new SupplierDialog(SuppliersController.this).setModels(selectedItems.get(0)).isEdit().show(root);
        });
        Window window = this.getScene().getWindow();
        menu.show(window,x,y);
    }

    
    
}
