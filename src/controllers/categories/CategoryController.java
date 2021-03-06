/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers.categories;

import Messages.CategoryMessage;
import Messages.ConfirmationMessage;
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
import ponospos.entities.Category;
import ponospos.entities.User;
import singletons.PonosExecutor;
import tasks.categories.CreateTask;
import tasks.categories.DeleteTask;
import tasks.categories.UpdateTask;
import tasks.categories.FetchAllTask;
import tasks.categories.SearchCategoryByNameTask;
import util.Role;

/**
 *
 * @author Sawmtea
 */
public class CategoryController extends AnchorPane implements 
        PonosControllerInterface,ConfirmDialog.ConfirmDialogListener,
        CategoryDialog.CategoryDialogListener{

    @FXML
    private JFXButton newBtn;
    @FXML
    private TableView<Category> categoryTable;
    @FXML
    private TableColumn<Category, Integer> idCol;
    @FXML
    private TableColumn<Category, String> nameCol;
    @FXML
    private TableColumn<Category, String> descCol;
    @FXML
    private TableColumn<Category, Category> actionCol;
    @FXML 
    private TextField searchField;
    @FXML
    private Button searchBtn;
    
    private ObservableList<Category> categories=FXCollections.observableArrayList();
    private StackPane root;
    private MaskerPane mask;
    
    
    public CategoryController(MaskerPane mask,StackPane root){
        this.root=root;
        this.mask=mask;
        try {
            FXMLLoader loader=new FXMLLoader();
            loader.setLocation(this.getClass().getResource("/views/categories/categories.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (IOException ex) {
            Logger.getLogger(CategoryController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void initDependencies() {
       
    }

    @Override
    public void initControls() {
       categoryTable.setItems(categories);
       idCol.setCellValueFactory(e->new SimpleObjectProperty<>(e.getValue().getId()));
       nameCol.setCellValueFactory(e->new SimpleStringProperty(e.getValue().getName()));
       descCol.setCellValueFactory(e->new SimpleStringProperty(e.getValue().getDescription()));
       actionCol.setCellFactory((TableColumn<Category, Category> param) -> 
            new TableCell<Category,Category>(){
                FontAwesomeIconView viewIcon=new FontAwesomeIconView(FontAwesomeIcon.EYE);
                FontAwesomeIconView editIcon=new FontAwesomeIconView(FontAwesomeIcon.EDIT);
                FontAwesomeIconView delIcon=new FontAwesomeIconView(FontAwesomeIcon.TRASH);
                Button viewBtn=new Button("",viewIcon);
                Button editBtn=new Button("",editIcon);
                Button delBtn=new Button("",delIcon);
                @Override
                protected void updateItem(Category item, boolean empty) {
                    super.updateItem(item, empty); 
                    if (empty) {
                        setText(null);
                        setGraphic(null);
                    }else{
                        viewBtn.setOnAction(e->new CategoryDialog(CategoryController.this).isView().setCategory(categories.get(getIndex())).show(root));
                        editBtn.setOnAction(e->{
                            new CategoryDialog(CategoryController.this).isToUpdate(categories.get(getIndex())).show(root);
                        });
                        delBtn.setOnAction(e->{
                            ConfirmDialog d=new ConfirmDialog(ConfirmationMessage.TITLE, ConfirmationMessage.MESSAGE);
                            Category model = categories.get(getIndex());
                            d.setModel(model);
                            d.setListener(CategoryController.this);
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
       searchField.textProperty().addListener(e->{
           doSearch();
       });
    }

    @Override
    public void hookupEvent() {
        searchBtn.setOnAction(e->doSearch());
        categoryTable.setRowFactory(p->{
            TableRow<Category> row=new TableRow();
            
            row.setOnMouseClicked(e->{
                List<Category> selectedItems=categoryTable.getSelectionModel().getSelectedItems();
                
                if ( e.getButton()==MouseButton.SECONDARY) {
                    displayPopOver(e.getScreenX(),e.getScreenY(),selectedItems);
                }else if(e.getClickCount()==2){
                    new CategoryDialog(CategoryController.this).isView().setCategory(categories.get(row.getIndex())).show(root);
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

    @FXML
    private void onNewBtnClick(ActionEvent event) {
        new CategoryDialog(this).isCreate(true).show(root);
    }

    @Override
    public void onClickYes(Object obj) {
        DeleteTask task=new DeleteTask();
        task.setCategory((Category) obj);
        mask.visibleProperty().bind(task.runningProperty());
        task.setOnSucceeded(e->{
            categories.remove(task.getValue());
            Notifications.create().title(CategoryMessage.CREATE_SUCCESS_TITLE).text(CategoryMessage.CREATE_SUCCESS_MESSAGE).showInformation();
                });
        task.setOnFailed(e->task.getException().printStackTrace(System.err));
        PonosExecutor.getInstance().getExecutor().submit(task);
    }

    @Override
    public void onCreate(Category category) {
        CreateTask task=new CreateTask();
        task.setCategory(category);
        mask.visibleProperty().bind(task.runningProperty());
        task.setOnSucceeded(e->{
            categories.add(task.getValue());
            Notifications.create().title(CategoryMessage.CREATE_SUCCESS_TITLE).text(CategoryMessage.CREATE_SUCCESS_MESSAGE).showInformation();
        });
        task.setOnFailed(e->task.getException().printStackTrace(System.err));
        PonosExecutor.getInstance().getExecutor().submit(task);
    }

    @Override
    public void onUpdate(Category category) {
        UpdateTask task=new UpdateTask();
        mask.visibleProperty().bind(task.runningProperty());
        task.setCategory(category);
        task.setOnSucceeded(e->{
            categoryTable.refresh();
            Notifications.create().title(CategoryMessage.UPDATE_SUCCESS_TITLE).text(CategoryMessage.UPDATE_SUCCESS_MESSAGE).showInformation();
        });
       task.setOnFailed(e->task.getException().printStackTrace(System.err));
       PonosExecutor.getInstance().getExecutor().submit(task);
    }

    public void fetchAllCategories() {
        FetchAllTask task=new FetchAllTask();
        mask.visibleProperty().bind(task.runningProperty());
        task.setOnFailed(e->task.getException().printStackTrace(System.err));
        task.setOnSucceeded(e->{
            categories.clear();
            categories.addAll(task.getValue());
        });
        PonosExecutor.getInstance().getExecutor().submit(task);
    }

    private void doSearch() {
        SearchCategoryByNameTask task=new SearchCategoryByNameTask();
        task.setOnSucceeded(e->{
            categories.clear();
            categories.addAll(task.getValue());
        });
        task.setName(searchField.getText().trim());
        task.setOnFailed(e->task.getException().printStackTrace(System.err));
        PonosExecutor.getInstance().getExecutor().submit(task);
    }
    
private void displayPopOver(double x,double y,List<Category> selectedItems){
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
        
        view.setOnAction(e->new CategoryDialog(CategoryController.this).isView().setCategory(selectedItems.get(0)).show(root));
        del.setOnAction(e->{
            for (Category selectedItem : selectedItems) {
                
                ConfirmDialog d=new ConfirmDialog(ConfirmationMessage.TITLE,ConfirmationMessage.MESSAGE+" "
                        + "" +selectedItem.getName());
                d.setModel(selectedItem);
                d.setListener(CategoryController.this);
               d.show(root);
                
            }
        });
        edit.setOnAction(e->{
            new CategoryDialog(CategoryController.this).isToUpdate(selectedItems.get(0)).show(root);
        });
        Window window = this.getScene().getWindow();
        menu.show(window,x,y);
    }

}