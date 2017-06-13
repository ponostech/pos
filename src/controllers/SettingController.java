/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextField;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import org.controlsfx.control.MaskerPane;
import util.PonosPreference;

/**
 * FXML Controller class
 *
 * @author Sawmtea
 */
public class SettingController extends AnchorPane implements PonosControllerInterface  {

    @FXML
    private JFXTextField taxField;
    @FXML
    private JFXButton taxBtn;
    @FXML
    private JFXRadioButton amountRadio;
    @FXML
    private JFXRadioButton percentRadio;
    private MaskerPane mask;
    private StackPane root;
    PonosPreference pref;
    public SettingController(MaskerPane mask,StackPane root){
        try {
            pref = new PonosPreference();
            this.mask=mask;
            this.root=root;
            FXMLLoader loader=new FXMLLoader();
            loader.setLocation(this.getClass().getResource("/views/setting.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
            taxBtn.disableProperty().bind(taxField.textProperty().isEmpty());
            percentRadio.setOnAction(e->{
                
                pref.setDiscountOption(true);
                    });
            amountRadio.setOnAction(e->{
                
                pref.setDiscountOption(false);
                    });
        } catch (IOException ex) {
            Logger.getLogger(SettingController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @FXML
    public void onTaxBtnClick(ActionEvent e){
        PonosPreference pref=new PonosPreference();
        pref.setTax(Float.parseFloat(taxField.getText()));
    }

    @Override
    public void initDependencies() {
    }

    @Override
    public void initControls() {
    }

    @Override
    public void bindControls() {
    }

    @Override
    public void hookupEvent() {
        taxField.setText(Float.toString(pref.getTax()));
        boolean percent = pref.getDiscountOption();
        if (percent) {  
            this.percentRadio.setSelected(true);
        }else{
            amountRadio.setSelected(true);
        }
    }

    @Override
    public void controlFocus() {
    }
    
    
}
