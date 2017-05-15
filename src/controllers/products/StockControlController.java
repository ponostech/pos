/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers.products;

import controllers.PonosControllerInterface;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import org.controlsfx.control.MaskerPane;

/**
 * FXML Controller class
 *
 * @author Sawmtea
 */
public class StockControlController extends AnchorPane implements
        PonosControllerInterface{

   
    private StackPane root;
    private MaskerPane mask;
    public StockControlController(MaskerPane mask,StackPane root){
        try {
            this.mask=mask;
            this.root=root;
            FXMLLoader loader=new FXMLLoader();
            loader.setLocation(this.getClass().getResource("/views/products/stock_control.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
            } catch (IOException ex) {
            Logger.getLogger(StockControlController.class.getName()).log(Level.SEVERE, null, ex);
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

    public void fetchStock() {
    }
}
