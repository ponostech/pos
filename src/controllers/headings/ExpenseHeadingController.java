/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers.headings;

import Messages.ConfirmationMessage;
import Messages.HeadingMessage;
import com.jfoenix.controls.JFXButton;
import controllers.PonosControllerInterface;
import controllers.expenditures.ExpenseDialog;
import controllers.modals.ConfirmDialog;
import controllers.settings.UnitSettingController;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import ponospos.entities.ExpenseHeading;
import ponospos.entities.Unit;
import ponospos.entities.User;
import singletons.PonosExecutor;
import tasks.headings.CreateHeading;
import tasks.headings.DeleteHeading;
import tasks.headings.FetchAllHeading;
import tasks.headings.UpdateHeading;
import views.settings.UnitDialogController;

/**
 * FXML Controller class
 *
 * @author Sawmtea
 */
public class ExpenseHeadingController extends AnchorPane implements PonosControllerInterface
,ConfirmDialog.ConfirmDialogListener,HeadingDialogController.ExpenseListener{

    @FXML
    private TextField searchField;
    @FXML
    private Button searchBtn;
    @FXML
    private JFXButton newBtn;
    @FXML
    private TableView<ExpenseHeading> expenseTable;
    @FXML
    private TableColumn<ExpenseHeading, Integer> idCol;
    @FXML
    private TableColumn<ExpenseHeading, String> textCol;
    @FXML
    private TableColumn<ExpenseHeading, User> userCol;
    @FXML
    private TableColumn<ExpenseHeading, ExpenseHeading> actionCol;

    private StackPane root;
    private MaskerPane mask;
    private ObservableList<ExpenseHeading> headings=FXCollections.observableArrayList();
    public ExpenseHeadingController(MaskerPane mask,StackPane root) {
        try {
            this.root=root;
            this.mask=mask;
            FXMLLoader l=new FXMLLoader();
            l.setLocation(this.getClass().getResource("/views/heading/expense_heading.fxml"));
            l.setController(this);
            l.setRoot(this);
            l.load();
        } catch (IOException ex) {
            Logger.getLogger(ExpenseHeadingController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void initDependencies() {
    }

    @Override
    public void initControls() {
        expenseTable.setItems(headings);
        idCol.setCellValueFactory(e->new SimpleObjectProperty<>(e.getValue().getId()));
        textCol.setCellValueFactory(e->new SimpleStringProperty(e.getValue().getText()));
        userCol.setCellValueFactory(e->new SimpleObjectProperty(e.getValue().getAddedBy()));
        actionCol.setCellFactory((TableColumn<ExpenseHeading, ExpenseHeading> param)
                -> new TableCell<ExpenseHeading, ExpenseHeading>() {
            FontAwesomeIconView editIcon = new FontAwesomeIconView(FontAwesomeIcon.EDIT);
            FontAwesomeIconView delIcon = new FontAwesomeIconView(FontAwesomeIcon.TRASH);
            Button editBtn = new Button("", editIcon);
            Button delBtn = new Button("", delIcon);

            @Override
            protected void updateItem(ExpenseHeading item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    editBtn.setOnAction(e -> {
                        HeadingDialogController d=new HeadingDialogController(ExpenseHeadingController.this);
                        d.isEdit();
                        d.setHead(headings.get(getIndex()));
                        d.show(root);
                    });
                    delBtn.setOnAction(e -> {
                        ConfirmDialog d = new ConfirmDialog(ConfirmationMessage.TITLE, ConfirmationMessage.MESSAGE);
                        ExpenseHeading model = headings.get(getIndex());
                        d.setModel(model);
                        d.setListener(ExpenseHeadingController.this);
                        d.show(root);
                    });
                    setGraphic(new HBox(5, editBtn, delBtn));
                }
            }

        }
        );
        newBtn.setOnAction(e->{
            HeadingDialogController d=new HeadingDialogController(this);
            d.isCreate();
            d.show(root);
        });
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

    @Override
    public void onClickYes(Object obj) {
        DeleteHeading task = new DeleteHeading();
        task.setOnSucceeded(e -> {
            Notifications.create().title(HeadingMessage.DELETE_TITLE).text(HeadingMessage.DELETE_MESSAGE).showInformation();
            headings.remove(task.getValue());
        });
        task.setOnFailed(e -> {
            task.getException().printStackTrace(System.err);
            ExceptionDialog ec = new ExceptionDialog(task.getException());
            ec.setOnCloseRequest(v -> {
                PonosExecutor.getInstance().getExecutor().shutdownNow();
                System.exit(1);
            });
        });
        task.head = (ExpenseHeading) obj;
        PonosExecutor.getInstance().getExecutor().submit(task);
    }

    @Override
    public void onCreate(ExpenseHeading head) {
        CreateHeading task=new CreateHeading();
        task.setOnSucceeded(e->{
            Notifications.create().title(HeadingMessage.CREATE_TITLE).text(HeadingMessage.CREATE_MESSAGE).showInformation();
            headings.add(task.getValue());
        });
        task.setOnFailed(e->{
            task.getException().printStackTrace(System.err);
            ExceptionDialog ec = new ExceptionDialog(task.getException());
            ec.setOnCloseRequest(v -> {
                PonosExecutor.getInstance().getExecutor().shutdownNow();
                System.exit(1);
            });
            ec.showAndWait();
        });
        task.head=head;
        PonosExecutor.getInstance().getExecutor().submit(task);
    }

    @Override
    public void onUpdate(ExpenseHeading head) {
        UpdateHeading task = new UpdateHeading();
        task.setOnSucceeded(e -> {
            Notifications.create().title(HeadingMessage.UPDATE_TITLE).text(HeadingMessage.UPDATE_MESSAGE).showInformation();
            expenseTable.refresh();
        });
        task.setOnFailed(e -> {
            task.getException().printStackTrace(System.err);
            ExceptionDialog ec = new ExceptionDialog(task.getException());
            ec.setOnCloseRequest(v -> {
                PonosExecutor.getInstance().getExecutor().shutdownNow();
                System.exit(1);
            });
            ec.showAndWait();
        });
        task.head = head;
        PonosExecutor.getInstance().getExecutor().submit(task);
    }

    public void fetchAll() {
        FetchAllHeading task = new FetchAllHeading();
        task.setOnSucceeded(e -> {
            headings.addAll(task.getValue());
        });
        task.setOnFailed(e -> {
            task.getException().printStackTrace(System.err);
            ExceptionDialog ec = new ExceptionDialog(task.getException());
            ec.setOnCloseRequest(v -> {
                PonosExecutor.getInstance().getExecutor().shutdownNow();
                System.exit(1);
            });
            ec.showAndWait();
        });
        PonosExecutor.getInstance().getExecutor().submit(task);
    }

  
    
    
}
