/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers.expenditures;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import ponospos.entities.Expenditure;
import ponospos.entities.ExpenseHeading;
import singletons.Auth;

/**
 * FXML Controller class
 *
 * @author Sawmtea
 */
public class ExpenseDialog extends JFXDialog {

    private boolean create;
    private boolean edit;
    private Expenditure expenditure;

    public interface ExpenseListener{
        public void onCreate(Expenditure expenditure);
        public void onUpdate(Expenditure expenditure);
    }
    @FXML
    private MaterialDesignIconView close;
    @FXML
    private Label title;
    @FXML
    private JFXComboBox<ExpenseHeading> headingCombo;
    @FXML
    private JFXTextArea descriptionField;
    @FXML
    private JFXTextField amountField;
    @FXML
    private JFXButton positivebtn;
    @FXML
    private JFXButton negativebtn;
    
    private ExpenseListener listener;

    public ExpenseDialog(ExpenseListener listener) {
        super();
        try {
            this.listener=listener;
            FXMLLoader l=new FXMLLoader();
            l.setLocation(this.getClass().getResource("/views/expenditures/ExpenseDialog.fxml"));
            l.setController(this);
            Region region = l.load();
            this.setContent(region);
            hookupEvent();
        } catch (IOException ex) {
            Logger.getLogger(ExpenseDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
    
    }
    public void isCreate(){
        this.create=true;
        this.title.setText("Create Expenditure");
        this.positivebtn.setText("Create");
    }
    public void isEdit(){
        this.edit=true;
        this.title.setText("Edit Expenditure");
        this.positivebtn.setText("Update");


    }
    public void setExpenditure(Expenditure exp){
        this.expenditure=exp;
        this.headingCombo.getSelectionModel().select(expenditure.getHead());
        this.descriptionField.setText(expenditure.getDescription());
        this.amountField.setText(Double.toString(expenditure.getAmount().doubleValue()));
    }
    private void hookupEvent(){
        this.setOnDialogOpened(e -> headingCombo.requestFocus());

        this.close.setOnMouseClicked(e->close());
        positivebtn.disableProperty().bind(amountField.textProperty().isEmpty());
        amountField.setOnKeyReleased(e->{
            if (!parseable(amountField.getText().trim())) {
                amountField.clear();
            }
        });
    }
    private boolean parseable(String str){
        try{
            Double.parseDouble(str);
            return true;
        }catch(Exception e){
            return false;
        }
    }
    @FXML
    public void onPositivebtnClick(ActionEvent e){
        if (create) {
            Expenditure exp = new Expenditure();
            exp.setHead(headingCombo.getSelectionModel().getSelectedItem());
            exp.setDescription(descriptionField.getText().trim());
            exp.setAmount(new BigDecimal(amountField.getText().trim()));
            exp.setCreatedAt(new Date(System.currentTimeMillis()));
            exp.setCreatedBy(Auth.getInstance().getUser());
            close();
            listener.onCreate(exp);
        }
        if (edit) {
            expenditure.setHead(headingCombo.getSelectionModel().getSelectedItem());
            expenditure.setDescription(descriptionField.getText().trim());
            expenditure.setAmount(new BigDecimal(amountField.getText().trim()));
            expenditure.setEditedAt(new Date(System.currentTimeMillis()));
            expenditure.setEditedBy(Auth.getInstance().getUser());
            close();
            listener.onUpdate(expenditure);
        }
    }
    @FXML
    public void onNegativeBtnClick(ActionEvent e){
        close();
    }
  
    
}
