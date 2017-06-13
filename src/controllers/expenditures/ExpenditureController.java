/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers.expenditures;

import Messages.ConfirmationMessage;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import controllers.PonosControllerInterface;
import controllers.modals.ConfirmDialog;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import org.controlsfx.control.MaskerPane;
import org.controlsfx.control.Notifications;
import org.controlsfx.dialog.ExceptionDialog;
import ponospos.entities.Expenditure;
import singletons.PonosExecutor;
import tasks.expenditures.CreateExpenditure;
import tasks.expenditures.DeleteExpenditure;
import tasks.expenditures.EditExpenditure;
import tasks.expenditures.FindAllExpenditure;

/**
 * FXML Controller class
 *
 * @author Sawmtea
 */
public class ExpenditureController extends AnchorPane 
        implements PonosControllerInterface,ConfirmDialog.ConfirmDialogListener,ExpenseDialog.ExpenseListener  {

    @FXML
    private JFXDatePicker filterDate;
    @FXML
    private JFXButton filterBtn;
    @FXML
    private JFXButton newBtn;
    @FXML
    private TableView<Expenditure> expTable;
    @FXML
    private TableColumn<Expenditure, Integer> idCol;
    @FXML
    private TableColumn<Expenditure, String> dateCol;
    @FXML
    private TableColumn<Expenditure, String> headCol;
    @FXML
    private TableColumn<Expenditure, String> descCol;
    @FXML
    private TableColumn<Expenditure, String> amountCol;
    @FXML
    private TableColumn<Expenditure, Expenditure> actionCol;
    
    private StackPane root;
    private MaskerPane mask;

    private ObservableList<Expenditure> expenditures=FXCollections.observableArrayList();
    public ExpenditureController(MaskerPane mask,StackPane root) {
        try {
            this.root=root;
            this.mask=mask;
            FXMLLoader loader=new FXMLLoader();
            loader.setLocation(this.getClass().getResource("/views/expenditures/expenditure.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        } catch (IOException ex) {
            Logger.getLogger(ExpenditureController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    @Override
    public void initDependencies() {
        
    }

    @Override
    public void initControls() {
        this.expTable.setItems(expenditures);
        this.idCol.setCellValueFactory(e->new SimpleObjectProperty<>(e.getValue().getId()));
        this.dateCol.setCellValueFactory(e->{
            SimpleDateFormat fm=new SimpleDateFormat("dd/MM/yy hh:mm a");
            String str=fm.format(e.getValue().getCreatedAt());
            return new SimpleStringProperty(str);
        });
        this.headCol.setCellValueFactory(e->{
            String str="";
            Expenditure head=e.getValue();
            if (head.getHead()!=null) {
                str=head.getHead().getText();
            }
            return new SimpleStringProperty(str);
                    });
        this.descCol.setCellValueFactory(e->new SimpleStringProperty(e.getValue().getDescription()));
        this.amountCol.setCellValueFactory(e->{
        
            Expenditure value = e.getValue();
            String str = NumberFormat.getCurrencyInstance(new Locale("en","in")).format(value.getAmount().doubleValue());
            return new SimpleStringProperty(str);
        });
        actionCol.setCellFactory((TableColumn<Expenditure, Expenditure> param)
                -> new TableCell<Expenditure, Expenditure>() {
            FontAwesomeIconView editIcon = new FontAwesomeIconView(FontAwesomeIcon.EDIT);
            FontAwesomeIconView delIcon = new FontAwesomeIconView(FontAwesomeIcon.TRASH);
            Button editBtn = new Button("", editIcon);
            Button delBtn = new Button("", delIcon);

            @Override
            protected void updateItem(Expenditure item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    editBtn.setOnAction(e -> {
                        ExpenseDialog d=new ExpenseDialog(ExpenditureController.this);
                        d.isEdit();
                        d.setExpenditure(expenditures.get(getIndex()));
                        d.show(root);
                    });
                    delBtn.setOnAction(e -> {
                        ConfirmDialog d = new ConfirmDialog(ConfirmationMessage.TITLE, ConfirmationMessage.MESSAGE);
                        Expenditure model = expenditures.get(getIndex());
                        d.setModel(model);
                        d.setListener(ExpenditureController.this);
                        d.show(root);
                    });
                    setGraphic(new HBox(5, editBtn, delBtn));
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
        newBtn.setOnAction(e->{
            ExpenseDialog d=new ExpenseDialog(this);
            d.isCreate();
//            d.hookupEvent();
            d.show(root);
        });
    }

    @Override
    public void controlFocus() {
    }

    public void fetchAll(){
         FindAllExpenditure task = new FindAllExpenditure();
        mask.visibleProperty().bind(task.runningProperty());
        task.setOnSucceeded(e -> {
            expenditures.clear();
            expenditures.addAll(task.getValue());
        });
        task.setOnFailed(e -> {
            task.getException().printStackTrace(System.err);
            ExceptionDialog d=new ExceptionDialog(task.getException());
            d.setOnCloseRequest(ev->{
                PonosExecutor.getInstance().getExecutor().shutdownNow();
            });
            d.showAndWait();
        });
        PonosExecutor.getInstance().getExecutor().submit(task);
    }

    @Override
    public void onCreate(Expenditure expenditure) {
        CreateExpenditure task=new CreateExpenditure();
        task.expenditure=expenditure;
        mask.visibleProperty().bind(task.runningProperty());
        task.setOnSucceeded(e->{
            Notifications.create().title("").text("").showInformation();
            expenditures.add(task.getValue());
        });
        task.setOnFailed(e->{
            task.getException().printStackTrace(System.err);
            new ExceptionDialog(task.getException()).showAndWait();
        });
        PonosExecutor.getInstance().getExecutor().submit(task);
    }

    @Override
    public void onUpdate(Expenditure expenditure) {
        EditExpenditure task = new EditExpenditure();
        task.exp = expenditure;
        mask.visibleProperty().bind(task.runningProperty());
        task.setOnSucceeded(e -> {
            Notifications.create().title("").text("").showInformation();
            expTable.refresh();
        });
        task.setOnFailed(e -> {
            task.getException().printStackTrace(System.err);
            new ExceptionDialog(task.getException()).showAndWait();
        });
        PonosExecutor.getInstance().getExecutor().submit(task);
    }

    @Override
    public void onClickYes(Object obj) {
        DeleteExpenditure task = new DeleteExpenditure();
        task.exp=(Expenditure) obj;
        mask.visibleProperty().bind(task.runningProperty());
        task.setOnSucceeded(e -> {
            Notifications.create().title("").text("").showInformation();
            expenditures.remove(task.getValue());
        });
        task.setOnFailed(e -> {
            task.getException().printStackTrace(System.err);
            new ExceptionDialog(task.getException()).showAndWait();
        });
        PonosExecutor.getInstance().getExecutor().submit(task);
    }
       
    
}
