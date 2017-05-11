/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers.modals;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;

/**
 *
 * @author Sawmtea
 */
public class ConfirmDialog extends JFXDialog {
    public interface ConfirmDialogListener{
        public void onClickYes(Object obj);
    }
   
    @FXML
    FontAwesomeIconView close;
    @FXML
    private  Label title;
    @FXML
    private  Label message;
    @FXML
    private  JFXButton yesBtn;
    @FXML
    private  JFXButton noBtn;
    private ConfirmDialogListener listener;
    private Object model;
    public ConfirmDialog(String title,String message) {
            super();
        try {
            FXMLLoader loader=new FXMLLoader();
            loader.setLocation(this.getClass().getResource("/views/modals/confirmation.fxml"));
            loader.setController(this);
            Region content = loader.load();
            this.title.setText(title);
            this.message.setText(message);
                    
            this.setContent(content);
            yesBtn.setOnAction(e->{
                        this.close();
                        listener.onClickYes(model);
                    });
            noBtn.setOnAction(e->this.close());
            close.setOnMouseClicked(e->this.close());
            this.setOnDialogOpened(e->noBtn.requestFocus());
        } catch (IOException ex) {
            Logger.getLogger(ConfirmDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    
    public JFXDialog title(String text){
        this.title.setText(text);
        return this;
    }
    
    public JFXDialog setMessage(String text){
        this.message.setText(text);
        return this;
    }

    public JFXDialog getConfirmDialog() {
        return this;
    }
    
    public JFXDialog setListener(ConfirmDialogListener listener){
        this.listener=listener;
        yesBtn.setOnAction(e->{
            this.close();
            listener.onClickYes(model);
        });
        return this;
    }
    public JFXDialog model(Object obj){
        this.model=obj;
        return this;
    }

    public void setModel(Object model) {
        this.model = model;
    }
    
    
    
   
}
