/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers.headings;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.validation.RequiredFieldValidator;
import de.jensd.fx.glyphs.GlyphsBuilder;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import ponospos.entities.ExpenseHeading;
import singletons.Auth;

/**
 * FXML Controller class
 *
 * @author Sawmtea
 */
public class HeadingDialogController extends JFXDialog{

    private boolean create;
    private boolean edit;
    public interface ExpenseListener{
        public void onCreate(ExpenseHeading head);
        public void onUpdate(ExpenseHeading head);
    }

    @FXML
    private JFXTextArea headField;
    @FXML
    private MaterialDesignIconView close;
    @FXML
    private Label title;
    @FXML
    private JFXButton positivebtn;
    @FXML
    private JFXButton negativebtn;

    private ExpenseListener listener;
    private ExpenseHeading head;

    public HeadingDialogController(ExpenseListener listener) {
        try {
            this.listener=listener;
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(this.getClass().getResource("/views/heading/heading_dialog.fxml"));
            loader.setController(this);
            Region region = loader.load();
            this.setContent(region);
            this.setOnDialogOpened(e->headField.requestFocus());
            this.close.setOnMouseClicked(e->close());
            this.negativebtn.setOnAction(e->close());
            validate();
            positivebtn.disableProperty().bind(headField.textProperty().isEmpty());
        } catch (IOException ex) {
            Logger.getLogger(HeadingDialogController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    public void isCreate(){
        this.create=true;
        this.title.setText("Create Heading");
        this.positivebtn.setText("Create");
    }
    public void isEdit(){
        this.edit=true;
        this.title.setText("Update Heading");
        this.positivebtn.setText("Update");
    }
    
    public void setHead(ExpenseHeading head){
        this.head=head;
        headField.setText(head.getText());
    }
    private void validate(){
        RequiredFieldValidator validator = new RequiredFieldValidator();
        validator.setMessage("Input Required");
        validator.setIcon(GlyphsBuilder.create(FontAwesomeIconView.class)
            .glyph(FontAwesomeIcon.WARNING)
            .size("1em")
            .styleClass("error")
            .build());
        this.headField.setValidators(validator);
        this.headField.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            if (!newValue) {
                this.headField.validate();
            }
        });
    }
   
    @FXML
    private void onPositivebtnClick(ActionEvent event) {
        if (create) {
            ExpenseHeading head=new ExpenseHeading();
            head.setText(headField.getText().trim());
            head.setAddedBy(Auth.getInstance().getUser());
            head.setCreatedAt(new Date(System.currentTimeMillis()));
            close();
            listener.onCreate(head);
        }
        if (edit) {
            head.setText(headField.getText().trim());
            
            close();
            listener.onUpdate(head);
        }
    }
    
}
