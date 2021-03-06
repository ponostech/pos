/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers.modals;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;
import singletons.PonosExecutor;

/**
 *
 * @author Sawmtea
 */
public class ExceptionDialog{
    @FXML 
    private TextArea stackTrace;
    
    @FXML 
    private Label errorMessage;
    @FXML 
    private Label errorCode;
    @FXML 
    private Button exitBtn;
    @FXML 
    private MaterialDesignIconView close;
    
    public ExceptionDialog(Throwable e){
        try {
            FXMLLoader loader=new FXMLLoader();
            loader.setLocation(this.getClass().getResource("/views/modals/exception_dialog.fxml"));
            loader.setController(this);
            Region content = loader.load();
           
            Dialog d=new Dialog();
            d.initModality(Modality.WINDOW_MODAL);
            DialogPane dialogPane=new DialogPane();
            dialogPane.setContent(content);
            
            d.setDialogPane(dialogPane);
            StringWriter sw=new StringWriter();
            PrintWriter pw=new PrintWriter(sw);
            e.printStackTrace(pw);
            stackTrace.setText(sw.toString());
            errorMessage.setText(e.getMessage());
//            errorCode.setText(e.getCause().getMessage());
            
            exitBtn.setOnAction(ev->{
                PonosExecutor.getInstance().getExecutor().shutdownNow();
                System.exit(0);
            });
            d.showAndWait();
        } catch (IOException ex) {
            Logger.getLogger(ConfirmDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
