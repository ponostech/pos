/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package views.settings;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import de.jensd.fx.glyphs.GlyphsBuilder;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import ponospos.entities.Unit;

/**
 * FXML Controller class
 *
 * @author Sawmtea
 */
public class UnitDialogController extends JFXDialog {

    @FXML
    private MaterialDesignIconView close;
    @FXML
    private Label title;
    @FXML
    private JFXTextField nameField;
    @FXML
    private JFXButton positiveBtn;
    @FXML
    private JFXButton negativeBtn;

    private boolean create;
    private boolean edit;

    private Unit unit;
    public interface UnitListener{
        public void onCreate(Unit unit);
        public void onUpdate(Unit unit);
    }
    private UnitListener listener;
    
    public UnitDialogController(UnitListener listener) {
        try {
            this.listener=listener;
            this.listener = listener;
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(this.getClass().getResource("/views/settings/unit_dialog.fxml"));
            loader.setController(this);
            Region region = loader.load();
            this.setContent(region);
            this.setOnDialogOpened(e -> this.nameField.requestFocus());
            close.setOnMouseClicked(e -> close());
            doValidate();
            this.positiveBtn.disableProperty().bind(nameField.textProperty().isEmpty());
            
        } catch (IOException ex) {
            Logger.getLogger(UnitDialogController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
        nameField.setText(unit.getName());
    }
    public void isCreate(){
        create=true;
        title.setText("Create Unit");
    }
    public void isEdit(){
        edit=true;       
        title.setText("Update Unit");

    }
    @FXML
    public void onPositivebtnClick(ActionEvent e){
        if (create) {
            Unit unit=new Unit();
            unit.setName(nameField.getText().trim());
            close();
            listener.onCreate(unit);
        }
        if (edit) {
            unit.setName(nameField.getText().trim());
            close();
            listener.onUpdate(unit);
        }
    }
    
    @FXML
    public void onNegativebtnClick(ActionEvent e){
        close();
    }
    
    public void doValidate() {
        RequiredFieldValidator validator = new RequiredFieldValidator();
        validator.setMessage("Input Required");
        validator.setIcon(GlyphsBuilder.create(FontAwesomeIconView.class)
                .glyph(FontAwesomeIcon.WARNING)
                .size("1em")
                .styleClass("error")
                .build());
        this.nameField.setValidators(validator);
        this.nameField.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            if (!newValue) {
                this.nameField.validate();
            }
        });
    }
    

       
    
}
