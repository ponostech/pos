/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers.settings;

import Messages.ConfirmationMessage;
import Messages.UnitMessage;
import com.jfoenix.controls.JFXButton;
import controllers.PonosControllerInterface;
import controllers.categories.CategoryController;
import controllers.categories.CategoryDialog;
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
import org.controlsfx.dialog.ExceptionDialog;
import ponospos.entities.Category;
import ponospos.entities.Unit;
import singletons.PonosExecutor;
import tasks.settings.CreateUnit;
import tasks.settings.DeleteUnit;
import tasks.settings.FetchAllUnit;
import views.settings.UnitDialogController;

/**
 * FXML Controller class
 *
 * @author Sawmtea
 */
public class UnitSettingController extends AnchorPane  implements UnitDialogController.UnitListener
,PonosControllerInterface,ConfirmDialog.ConfirmDialogListener{

    @FXML
    private TextField searchField;
    @FXML
    private Button searchBtn;
    @FXML
    private JFXButton newUnitBtn;
    @FXML
    private TableView<Unit> unitTable;
    @FXML
    private TableColumn<Unit, Integer> idCol;
    @FXML
    private TableColumn<Unit, String> nameCol;
    @FXML
    private TableColumn<Unit, Unit> actionCol;

    private StackPane root;
    private MaskerPane mask;
    
    private ObservableList<Unit> units=FXCollections.observableArrayList();
    
    public UnitSettingController(MaskerPane mask,StackPane root) {
        try {
            this.mask=mask;
            this.root=root;
            FXMLLoader l=new FXMLLoader();
            l.setLocation(this.getClass().getResource("/views/settings/unit_setting.fxml"));
            l.setController(this);
            l.setRoot(this);
            l.load();
        } catch (IOException ex) {
            Logger.getLogger(UnitSettingController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    @FXML
    private void onNewClick(ActionEvent event) {
        UnitDialogController d=new UnitDialogController(this);
        d.isCreate();
        d.show(root);
    }

    @Override
    public void onCreate(Unit unit) {
        CreateUnit task=new CreateUnit();
        task.unit = unit;
        task.setOnFailed(e->{
            task.getException().printStackTrace(System.err);
            ExceptionDialog ec=new ExceptionDialog(task.getException());
            ec.setOnCloseRequest(v->{
                PonosExecutor.getInstance().getExecutor().shutdownNow();
                System.exit(1);
            });
        });
        task.setOnSucceeded(e->{
            Notifications.create().title(UnitMessage.CREATE_TITLE).text(UnitMessage.CREATE_MESSAGE).showInformation();
            units.add(task.getValue());
        });
        mask.visibleProperty().bind(task.runningProperty());
        PonosExecutor.getInstance().getExecutor().submit(task);
    }

    @Override
    public void onUpdate(Unit unit) {
        CreateUnit task = new CreateUnit();
        task.unit=unit;
        task.setOnFailed(e -> {
            task.getException().printStackTrace(System.err);
            ExceptionDialog ec = new ExceptionDialog(task.getException());
            ec.setOnCloseRequest(v -> {
                PonosExecutor.getInstance().getExecutor().shutdownNow();
                System.exit(1);
            });
        });
        task.setOnSucceeded(e -> {
            Notifications.create().title(UnitMessage.UPDATE_TITLE).text(UnitMessage.UPDATE_MESSAGE).showInformation();
            unitTable.refresh();
        });
        mask.visibleProperty().bind(task.runningProperty());
        PonosExecutor.getInstance().getExecutor().submit(task);
    }

    @Override
    public void initDependencies() {
    }

    @Override
    public void initControls() {
        unitTable.setItems(units);
        idCol.setCellValueFactory(e->new SimpleObjectProperty<>(e.getValue().getId()));
        nameCol.setCellValueFactory(e->new SimpleStringProperty(e.getValue().getName()));
        actionCol.setCellFactory((TableColumn<Unit, Unit> param)
                -> new TableCell<Unit, Unit>() {
            FontAwesomeIconView editIcon = new FontAwesomeIconView(FontAwesomeIcon.EDIT);
            FontAwesomeIconView delIcon = new FontAwesomeIconView(FontAwesomeIcon.TRASH);
            Button editBtn = new Button("", editIcon);
            Button delBtn = new Button("", delIcon);

            @Override
            protected void updateItem(Unit item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    editBtn.setOnAction(e -> {
                        UnitDialogController c=new UnitDialogController(UnitSettingController.this);
                        c.setUnit(units.get(getIndex()));
                        c.isEdit();
                        c.show(root);
                    });
                    delBtn.setOnAction(e -> {
                        ConfirmDialog d = new ConfirmDialog(ConfirmationMessage.TITLE, ConfirmationMessage.MESSAGE);
                        Unit model = units.get(getIndex());
                        d.setModel(model);
                        d.setListener(UnitSettingController.this);
                        d.show(root);
                    });
                    setGraphic(new HBox(5,  editBtn, delBtn));
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
    
    public void fetchAll(){
        FetchAllUnit task = new FetchAllUnit();
        task.setOnFailed(e -> {
            task.getException().printStackTrace(System.err);
            ExceptionDialog ec = new ExceptionDialog(task.getException());
            ec.setOnCloseRequest(v -> {
                PonosExecutor.getInstance().getExecutor().shutdownNow();
                System.exit(1);
            });
        });
        task.setOnSucceeded(e -> {
            units.clear();
            units.addAll(task.getValue());
        });
        mask.visibleProperty().bind(task.runningProperty());
        PonosExecutor.getInstance().getExecutor().submit(task);
    }

    @Override
    public void onClickYes(Object obj) {
        DeleteUnit task = new DeleteUnit();
        task.unit=(Unit) obj;
        task.setOnFailed(e -> {
            task.getException().printStackTrace(System.err);
            ExceptionDialog ec = new ExceptionDialog(task.getException());
            ec.setOnCloseRequest(v -> {
                PonosExecutor.getInstance().getExecutor().shutdownNow();
                System.exit(1);
            });
        });
        task.setOnSucceeded(e -> {
            Notifications.create().title(UnitMessage.DELETE_TITLE).text(UnitMessage.DELETE_MESSAGE).showInformation();
            units.remove(task.getValue());
        });
        mask.visibleProperty().bind(task.runningProperty());
        PonosExecutor.getInstance().getExecutor().submit(task);
    }

   
    
}
