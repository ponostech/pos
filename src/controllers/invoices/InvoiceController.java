/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers.invoices;

import controllers.PonosControllerInterface;
import controllers.SettingController;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import org.controlsfx.control.MaskerPane;

/**
 *
 * @author Sawmtea
 */
public class InvoiceController extends AnchorPane implements PonosControllerInterface{
    private MaskerPane mask;
    private StackPane root;
    
    public InvoiceController(MaskerPane mask,StackPane root){
        try {
            this.mask=mask;
            this.root=root;
            FXMLLoader loader=new FXMLLoader();
            loader.setLocation(this.getClass().getResource("/views/invoices/invoices.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
                    } catch (IOException ex) {
            Logger.getLogger(SettingController.class.getName()).log(Level.SEVERE, null, ex);
        }
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
    }

    @Override
    public void controlFocus() {
    }

    public void fetchAll() {
    }
}
