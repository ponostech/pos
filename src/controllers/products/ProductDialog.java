/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers.products;

import com.jfoenix.controls.JFXDialog;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Region;

/**
 * FXML Controller class
 *
 * @author Sawmtea
 */
public class ProductDialog extends JFXDialog {
    public interface ProductDialogListener{
        //public void onCreate(Product product);
        //public void onUpdate(Product product);
    }
    
    private boolean isView;
    private boolean isCreate;
    private boolean isEdit;
    private ProductDialogListener listener;
    
    public ProductDialog(ProductDialogListener listener){
        try {
            this.listener=listener;
            FXMLLoader loader=new FXMLLoader();
            loader.setLocation(this.getClass().getResource("/views/products/product_dialog.fxml"));
            loader.setController(this);
            Region region = loader.load();
            this.setContent(region);
        } catch (IOException ex) {
            Logger.getLogger(ProductDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public ProductDialog isView(){
        this.isView=true;
        
        return this;
    }
    public ProductDialog isCreate(){
        this.isCreate=true;
        
        return this;
    }
    public ProductDialog isEdit(){
        this.isEdit=true;
        
        return this;
    }
   
    
    
}
